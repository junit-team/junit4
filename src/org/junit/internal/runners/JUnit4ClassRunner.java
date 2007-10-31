package org.junit.internal.runners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.internal.runners.links.ExpectException;
import org.junit.internal.runners.links.Fail;
import org.junit.internal.runners.links.FailOnTimeout;
import org.junit.internal.runners.links.IgnoreTestNotifier;
import org.junit.internal.runners.links.IgnoreViolatedAssumptions;
import org.junit.internal.runners.links.InvokeMethod;
import org.junit.internal.runners.links.Notifier;
import org.junit.internal.runners.links.RunAfters;
import org.junit.internal.runners.links.RunBefores;
import org.junit.internal.runners.links.RunTestNotifier;
import org.junit.internal.runners.links.Statement;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.InitializationError;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.model.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class JUnit4ClassRunner extends ParentRunner<TestMethod> implements Filterable, Sortable {
	protected final List<TestMethod> fTestMethods;

	public JUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
		fTestMethods= computeTestMethods();
		validate();
	}

	protected List<TestMethod> computeTestMethods() {
		return fTestClass.getTestMethods();
	}

	private void validate() throws InitializationError {
		List<Throwable> errors= new ArrayList<Throwable>();
		collectInitializationErrors(errors);
		assertValid(errors);
	}

	protected void collectInitializationErrors(List<Throwable> errors) {
		fTestClass.validateMethodsForDefaultRunner(errors);
	}

	@Override
	protected Statement classBlock(final RunNotifier notifier) {
		return new Statement() {
			@Override
			public void evaluate() {
				for (TestMethod method : getChildren())
					runChild(method, notifier);
			}
		};
	}

	protected void runChild(TestMethod method, RunNotifier notifier) {
		Description description= describeChild(method);
		EachTestNotifier eachNotifier= new EachTestNotifier(notifier,
				description);
		notifying(method, childBlock(method)).run(eachNotifier);
	}

	public Object createTest() throws Exception {
		return fTestClass.getConstructor().newInstance();
	}

	@Override
	protected Description describeChild(TestMethod method) {
		return Description.createTestDescription(fTestClass.getJavaClass(),
				testName(method), method.getMethod().getAnnotations());
	}

	protected String testName(TestMethod method) {
		return method.getName();
	}

	public Statement childBlock(TestMethod method) {
		Object test;
		try {
			// TODO: (Oct 12, 2007 11:49:18 AM) Can I ditch reflective callable?

			test= new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
		} catch (Throwable e) {
			return new Fail(e);
		}

		Statement link= invoke(method, test);
		link= possiblyExpectingExceptions(method, link);
		link= withPotentialTimeout(method, link);
		link= withBefores(method, test, link);
		link= ignoreViolatedAssumptions(link);
		link= withAfters(method, test, link);
		return link;
	}

	protected Statement invoke(TestMethod method, Object test) {
		return new InvokeMethod(method, test);
	}

	protected Statement ignoreViolatedAssumptions(Statement next) {
		return new IgnoreViolatedAssumptions(next);
	}

	protected Statement possiblyExpectingExceptions(TestMethod method,
			Statement next) {
		return method.expectsException() ? new ExpectException(next, method
				.getExpectedException()) : next;
	}

	protected Statement withPotentialTimeout(TestMethod method, Statement next) {
		long timeout= method.getTimeout();
		return timeout > 0 ? new FailOnTimeout(next, timeout) : next;
	}

	protected Statement withAfters(TestMethod method, Object target,
			Statement link) {
		// TODO: (Oct 12, 2007 10:23:59 AM) Check for DUP in callers

		return new RunAfters(link, method, target);
	}

	protected Statement withBefores(TestMethod method, Object target,
			Statement link) {
		return new RunBefores(link, method, target);
	}

	protected Notifier notifying(TestMethod method, Statement link) {
		return method.isIgnored() ? new IgnoreTestNotifier()
				: new RunTestNotifier(link);
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<TestMethod> iter= fTestMethods.iterator(); iter.hasNext();) {
			TestMethod method= iter.next();
			if (!filter.shouldRun(describeChild(method)))
				iter.remove();
		}
		if (fTestMethods.isEmpty())
			throw new NoTestsRemainException();
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fTestMethods, new Comparator<TestMethod>() {
			public int compare(TestMethod o1, TestMethod o2) {
				return sorter.compare(describeChild(o1),
						describeChild(o2));
			}
		});
	}

	@Override
	protected List<TestMethod> getChildren() {
		return fTestMethods;
	}
}
