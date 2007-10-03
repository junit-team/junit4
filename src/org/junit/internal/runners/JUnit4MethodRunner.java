package org.junit.internal.runners;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Assume.AssumptionViolatedException;

public class JUnit4MethodRunner extends JavaElement {
	protected final TestMethod fTestMethod;

	private final TestClass fTestClass;

	public JUnit4MethodRunner(Method method, TestClass testClass) {
		fTestMethod = new TestMethod(method);
		fTestClass= testClass;
	}

	protected void run(Roadie context) {
		runWithNotification(context);
	}

	protected void runWithNotification(Roadie context) {
		if (fTestMethod.isIgnored()) {
			context.fireTestIgnored();
			return;
		}
		context.fireTestStarted();
		try {
			runInsideNotification(context);
		} finally {
			context.fireTestFinished();
		}
	}

	protected void runInsideNotification(Roadie context) {
		runWithBeforeAndAfter(context);
	}

	protected void runWithBeforeAndAfter(final Roadie context) {
		context.runProtected(this, new Runnable() {
			public void run() {
				runInsideBeforeAndAfter(context);
			}
		});
	}

	protected void runInsideBeforeAndAfter(final Roadie context) {
		runWithPotentialTimeout(context);
	}
	
	protected void runWithPotentialTimeout(Roadie context) {
		long timeout= fTestMethod.getTimeout();
		if (timeout > 0)
			runWithActualTimeout(context, timeout);
		else
			runInsidePotentialTimeout(context);
	}
	
	protected void runInsidePotentialTimeout(final Roadie context) {
		runWithExpectedExceptionCheck(context);
	}

	protected void runWithExpectedExceptionCheck(final Roadie context) {
		try {
			runInsideExpectedExceptionCheck(context);

			fTestMethod.assertNoExceptionExpected(context);
		} catch (Throwable e) {
			if (e instanceof AssumptionViolatedException) {
				// do nothing
			} else 
				fTestMethod.assertExceptionExpected(context, e);
		}
	}

	protected void runInsideExpectedExceptionCheck(final Roadie context)
			throws Throwable {
		ExplosiveMethod.from(fTestMethod.getMethod()).invoke(context.getTarget());
	}

	protected void runWithActualTimeout(final Roadie context, final long timeout) {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				runInsidePotentialTimeout(context);
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
			result.get(0, TimeUnit.MILLISECONDS); // throws the
			// exception if one
			// occurred during
			// the invocation
		} catch (TimeoutException e) {
			context.addFailure(new Exception(String.format(
					"test timed out after %d milliseconds", timeout)));
		} catch (Exception e) {
			context.addFailure(e);
		}
	}

	@Override
	public List<Method> getBefores() {
		return fTestClass.getAnnotatedMethods(Before.class);
	}

	@Override
	public List<Method> getAfters() {
		return fTestClass.getAnnotatedMethods(After.class);
	}
}