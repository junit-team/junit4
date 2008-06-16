package org.junit.internal.runners;

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
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.model.TestAnnotation;
import org.junit.internal.runners.model.TestClass;
import org.junit.internal.runners.model.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.notification.RunNotifier;

public class BlockJUnit4ClassRunner extends ParentRunner<FrameworkMethod> implements Filterable, Sortable {
	protected final List<FrameworkMethod> fTestMethods;

	public BlockJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		this(new TestClass(klass));
	}
	
	public BlockJUnit4ClassRunner(TestClass testClass) throws InitializationError {
		super(testClass);
		fTestMethods= computeTestMethods();
		validate();
	}
	
	//
	// Override in subclasses
	//

	protected List<FrameworkMethod> computeTestMethods() {
		return getTestClass().getTestMethods();
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		getTestClass().validateMethodsForDefaultRunner(errors);
	}

	protected Object createTest() throws Exception {
		return getTestClass().getConstructor().newInstance();
	}

	protected String testName(FrameworkMethod method) {
		return method.getName();
	}
	
	protected Statement childBlock(FrameworkMethod method) {
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
	
	//
	// Implementation of ParentRunner
	// 
	
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		Description description= describeChild(method);
		EachTestNotifier eachNotifier= new EachTestNotifier(notifier,
				description);
		notifying(method, childBlock(method)).run(eachNotifier);
	}

	@Override
	protected Description describeChild(FrameworkMethod method) {
		return Description.createTestDescription(getTestClass().getJavaClass(),
				testName(method), method.getMethod().getAnnotations());
	}

	@Override
	protected List<FrameworkMethod> getChildren() {
		return fTestMethods;
	}

	//
	// Statement builders
	//
	
	protected Statement invoke(FrameworkMethod method, Object test) {
		return new InvokeMethod(method, test);
	}

	protected Statement possiblyExpectingExceptions(TestAnnotation annotation,
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
		return new RunAfters(link, new TestMethod(getTestClass()), target);
	}

	protected Statement withBefores(FrameworkMethod method, Object target,
			Statement link) {
		return new RunBefores(link, new TestMethod(getTestClass()), target);
	}

	protected Notifier notifying(FrameworkMethod method, Statement link) {
		return method.isIgnored() ? new IgnoreTestNotifier()
				: new RunTestNotifier(link);
	}
}
