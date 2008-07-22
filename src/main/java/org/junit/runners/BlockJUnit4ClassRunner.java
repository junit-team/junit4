package org.junit.runners;

import java.util.List;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.ParentRunner; // TODO: publish?
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.model.TestClass;
import org.junit.internal.runners.model.TestMethod;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class BlockJUnit4ClassRunner extends ParentRunner<FrameworkMethod> implements Filterable, Sortable {
	protected final List<FrameworkMethod> fTestMethods;

	public BlockJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(new TestClass(klass));
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

		Statement link= invoke(method, test);
		link= possiblyExpectingExceptions(method, test, link);
		link= withPotentialTimeout(method, test, link);
		link= withBefores(method, test, link);
		link= withAfters(method, test, link);
		return link;
	}
	
	//
	// Implementation of ParentRunner
	// 
	
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		EachTestNotifier eachNotifier= makeNotifier(method, notifier);
		if (method.isIgnored()) {
			eachNotifier.fireTestIgnored();
			return;
		}
		
		eachNotifier.fireTestStarted();
		try {
			childBlock(method).evaluate();
		} catch (AssumptionViolatedException e) {
			// do nothing: same as passing (for 4.5; may change in 4.6)
		} catch (Throwable e) {
			eachNotifier.addFailure(e);
		} finally {
			eachNotifier.fireTestFinished();
		}
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

	protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test,
			Statement next) {
		Test annotation= getAnnotation(method);
		return expectsException(annotation) ? new ExpectException(next, getExpectedException(annotation)) : next;
	}

	protected Statement withPotentialTimeout(FrameworkMethod method, Object test,
			Statement next) {
		long timeout= getTimeout(getAnnotation(method));
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

	private EachTestNotifier makeNotifier(FrameworkMethod method,
			RunNotifier notifier) {
		Description description= describeChild(method);
		return new EachTestNotifier(notifier,
				description);
	}

	private Class<? extends Throwable> getExpectedException(Test annotation) {
		if (annotation == null || annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

	private boolean expectsException(Test annotation) {
		return getExpectedException(annotation) != null;
	}

	private long getTimeout(Test annotation) {
		if (annotation == null)
			return 0;
		return annotation.timeout();
	}
	
	private Test getAnnotation(FrameworkMethod method) {
		return method.getMethod().getAnnotation(Test.class);
	}

}
