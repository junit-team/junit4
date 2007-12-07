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
import org.junit.internal.runners.links.InvokeMethod;
import org.junit.internal.runners.links.Notifier;
import org.junit.internal.runners.links.RunAfters;
import org.junit.internal.runners.links.RunBefores;
import org.junit.internal.runners.links.RunTestNotifier;
import org.junit.internal.runners.links.Statement;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.FrameworkMethod;
import org.junit.internal.runners.model.InitializationError;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.model.TestAnnotation;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class JUnit4ClassRunner extends ParentRunner<FrameworkMethod> implements Filterable, Sortable {
	protected final List<FrameworkMethod> fTestMethods;

	public JUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
		fTestMethods= computeTestMethods();
		validate();
	}

	protected List<FrameworkMethod> computeTestMethods() {
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
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		Description description= describeChild(method);
		EachTestNotifier eachNotifier= new EachTestNotifier(notifier,
				description);
		notifying(method, childBlock(method)).run(eachNotifier);
	}

	public Object createTest() throws Exception {
		return fTestClass.getConstructor().newInstance();
	}

	@Override
	protected Description describeChild(FrameworkMethod method) {
		return Description.createTestDescription(fTestClass.getJavaClass(),
				testName(method), method.getMethod().getAnnotations());
	}

	protected String testName(FrameworkMethod method) {
		return method.getName();
	}

	public Statement childBlock(FrameworkMethod method) {
		Object test;
		try {
			test= new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
		} catch (Throwable e) {
			return new Fail(e);
		}

		TestAnnotation annotation= new TestAnnotation(method);
		
		Statement link= invoke(method, test);
		link= possiblyExpectingExceptions(annotation, link);
		link= withPotentialTimeout(annotation, link);
		link= withBefores(method, test, link);
		link= withAfters(method, test, link);
		return link;
	}

	protected Statement invoke(FrameworkMethod method, Object test) {
		return new InvokeMethod(method, test);
	}

	private Statement possiblyExpectingExceptions(TestAnnotation annotation,
			Statement next) {
		return annotation.expectsException() ? new ExpectException(next, annotation
				.getExpectedException()) : next;
	}

	protected Statement withPotentialTimeout(TestAnnotation annotation,
			Statement next) {
		long timeout= annotation.getTimeout();
		return timeout > 0 ? new FailOnTimeout(next, timeout) : next;
	}

	protected Statement withAfters(FrameworkMethod method, Object target,
			Statement link) {
		return new RunAfters(link, new TestMethodElement(getTestClass()), target);
	}

	protected Statement withBefores(FrameworkMethod method, Object target,
			Statement link) {
		return new RunBefores(link, new TestMethodElement(getTestClass()), target);
	}

	protected Notifier notifying(FrameworkMethod method, Statement link) {
		return method.isIgnored() ? new IgnoreTestNotifier()
				: new RunTestNotifier(link);
	}

	// TODO: (Dec 1, 2007 11:37:28 PM) absorb into parent?

	@Override
	public void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<FrameworkMethod> iter= fTestMethods.iterator(); iter.hasNext();) {
			FrameworkMethod method= iter.next();
			if (!filter.shouldRun(describeChild(method)))
				iter.remove();
		}
		if (fTestMethods.isEmpty())
			throw new NoTestsRemainException();
	}

	@Override
	public void sort(final Sorter sorter) {
		Collections.sort(fTestMethods, new Comparator<FrameworkMethod>() {
			public int compare(FrameworkMethod o1, FrameworkMethod o2) {
				return sorter.compare(describeChild(o1),
						describeChild(o2));
			}
		});
	}

	@Override
	protected List<FrameworkMethod> getChildren() {
		return fTestMethods;
	}
}
