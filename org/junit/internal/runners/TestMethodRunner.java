package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.Failure;

public class TestMethodRunner extends BeforeAndAfterRunner {
	private final Object fTest;
	private final Method fMethod;
	private final RunNotifier fNotifier;
	private final TestIntrospector fTestIntrospector;
	private final Description fDescription;

	public TestMethodRunner(Object test, Method method, RunNotifier notifier, Description description) {
		super(test.getClass(), Before.class, After.class, test);
		fTest= test;
		fMethod= method;
		fNotifier= notifier;
		fTestIntrospector= new TestIntrospector(test.getClass());
		fDescription= description;
	}

	public void run() {
		if (fTestIntrospector.isIgnored(fMethod)) {
			fNotifier.fireTestIgnored(fDescription);
			return;
		}
		fNotifier.fireTestStarted(fDescription);
		try {
			long timeout= fTestIntrospector.getTimeout(fMethod);
			if (timeout > 0)
				runWithTimeout(timeout);
			else
				runMethod();
		} finally {
			fNotifier.fireTestFinished(fDescription);
		}
	}

	private void runWithTimeout(long timeout) {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				runMethod();
				return null;
			}
		};
		Future<Object> result= service.submit(callable);
		service.shutdown();
		try {
			boolean terminated= service.awaitTermination(timeout,
					TimeUnit.MILLISECONDS);
			if (!terminated)
				service.shutdownNow();
			result.get(timeout, TimeUnit.MILLISECONDS); // throws the exception if one occurred during the invocation
		} catch (TimeoutException e) {
			addFailure(new Exception(String.format("test timed out after %d milliseconds", timeout)));
		} catch (Exception e) {
			addFailure(e);
		}		
	}
	
	private void runMethod() {
		runProtected();
	}
	
	@Override
	protected void runUnprotected() {
		try {
			executeMethodBody();
			if (expectsException())
				addFailure(new AssertionError("Expected exception: " + expectedException().getName()));
		} catch (InvocationTargetException e) {
			Throwable actual= e.getTargetException();
			if (!expectsException())
				addFailure(actual);
			else if (isUnexpected(actual)) {
				String message= "Unexpected exception, expected<" + expectedException().getName() + "> but was<"
					+ actual.getClass().getName() + ">";
				addFailure(new Exception(message, actual));
			}
		} catch (Throwable e) {
			addFailure(e);
		}
	}

	protected void executeMethodBody() throws IllegalAccessException, InvocationTargetException {
		fMethod.invoke(fTest);
	}

	@Override
	protected void addFailure(Throwable e) {
		fNotifier.fireTestFailure(new Failure(fDescription, e));
	}
	
	private boolean expectsException() {
		return expectedException() != null;
	}

	private Class<? extends Throwable> expectedException() {
		return fTestIntrospector.expectedException(fMethod);
	}

	private boolean isUnexpected(Throwable exception) {
		return ! expectedException().isAssignableFrom(exception.getClass());
	}
}

