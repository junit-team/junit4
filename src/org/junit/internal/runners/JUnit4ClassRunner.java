package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.internal.runners.links.ExpectingException;
import org.junit.internal.runners.links.IgnoreTest;
import org.junit.internal.runners.links.IgnoreViolatedAssumptions;
import org.junit.internal.runners.links.Invoke;
import org.junit.internal.runners.links.Link;
import org.junit.internal.runners.links.Notifying;
import org.junit.internal.runners.links.WithBeforeAndAfter;
import org.junit.internal.runners.links.WithTimeout;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ErrorList;
import org.junit.internal.runners.model.InitializationError;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.model.TestClass;
import org.junit.internal.runners.model.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

public class JUnit4ClassRunner extends Runner implements Filterable, Sortable {
	private final List<TestMethod> fTestMethods;

	private TestClass fTestClass;

	public JUnit4ClassRunner(Class<?> klass) throws InitializationError {
		fTestClass= new TestClass(klass);
		fTestMethods= getTestMethods();
		validate();
	}

	protected List<TestMethod> getTestMethods() {
		return fTestClass.getTestMethods();
	}

	private void validate() throws InitializationError {
		ErrorList errors= new ErrorList();
		collectInitializationErrors(errors);
		errors.assertEmpty();
	}

	protected void collectInitializationErrors(ErrorList errors) {
		fTestClass.validateMethodsForDefaultRunner(errors);
	}

	@Override
	public void run(final RunNotifier notifier) {
		fTestClass.runProtected(notifier, getDescription(), new Runnable() {
			public void run() {
				runMethods(notifier);
			}
		});
	}

	protected void runMethods(final RunNotifier notifier) {
		for (TestMethod method : fTestMethods)
			runMethod(method, notifier);
	}

	protected void runMethod(TestMethod method, RunNotifier notifier) {
		Description description= methodDescription(method);
		Object test;
		try {
			test= new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
		} catch (Throwable e) {
			notifier.testAborted(description, e);
			return;
		}
		EachTestNotifier roadie= new EachTestNotifier(notifier, description);
		run(roadie, method, test);
	}

	public Object createTest() throws Exception {
		return fTestClass.getConstructor().newInstance();
	}

	protected Description methodDescription(TestMethod method) {
		return Description.createTestDescription(fTestClass.getJavaClass(),
				testName(method), method.getMethod().getAnnotations());
	}

	protected String testName(TestMethod method) {
		return method.getName();
	}
	
	public void run(EachTestNotifier context, TestMethod method, Object test) {
		try {
			chain(method, test).run(context);
		} catch (StoppedByUserException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException("Unexpected error running tests", e);
		}
	}

	protected Link chain(TestMethod method, Object test) {
		// TODO: (Oct 5, 2007 11:09:00 AM) Rename Link?

		// TODO: (Oct 9, 2007 2:12:24 PM) method + test is parameter object?

		Link link= invoke(method, test);
		link= possiblyExpectingExceptions(method, link);
		link= ignoreViolatedAssumptions(link);
		link= withPotentialTimeout(method, link);
		link= withBeforeAndAfter(method, link, test);
		return notifying(method, link);
	}
	
	protected Link invoke(TestMethod method, Object test) {
		return new Invoke(method, test);
	}
	
	protected Link ignoreViolatedAssumptions(Link next) {
		return new IgnoreViolatedAssumptions(next);
	}

	protected Link possiblyExpectingExceptions(TestMethod method, Link next) {
		return method.expectsException()
			? new ExpectingException(next, method.getExpectedException())
			: next;
	}

	protected Link withPotentialTimeout(TestMethod method, Link next) {
		long timeout= method.getTimeout();
		return timeout > 0
			? new WithTimeout(next, timeout)
			: next;
	}

	protected Link withBeforeAndAfter(TestMethod method, Link link, Object target) {
		return new WithBeforeAndAfter(link, method, target);
	}

	protected Link notifying(TestMethod method, Link link) {
		return method.isIgnored()
			? new IgnoreTest()
			: new Notifying(link);
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(getName(),
				classAnnotations());
		List<TestMethod> testMethods= fTestMethods;

		for (TestMethod method : testMethods)
			spec.addChild(methodDescription(method));
		return spec;
	}

	protected String getName() {
		return fTestClass.getName();
	}
	
	protected Annotation[] classAnnotations() {
		return fTestClass.getJavaClass().getAnnotations();
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<TestMethod> iter= fTestMethods.iterator(); iter.hasNext();) {
			TestMethod method= iter.next();
			if (!filter.shouldRun(methodDescription(method)))
				iter.remove();
		}
		if (fTestMethods.isEmpty())
			throw new NoTestsRemainException();
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fTestMethods, new Comparator<TestMethod>() {
			public int compare(TestMethod o1, TestMethod o2) {
				return sorter.compare(methodDescription(o1),
						methodDescription(o2));
			}
		});
	}

	protected TestClass getTestClass() {
		return fTestClass;
	}
}