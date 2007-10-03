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
		fTestMethod= new TestMethod(method);
		fTestClass= testClass;
	}

	public abstract class Link {
		public abstract void run(Roadie context);
	}

	public class Notifier extends Link {
		private final Link fNext;

		public Notifier(Link next) {
			fNext= next;
		}

		@Override
		public void run(Roadie context) {
			if (fTestMethod.isIgnored()) {
				context.fireTestIgnored();
				return;
			}
			context.fireTestStarted();
			try {
				fNext.run(context);
			} finally {
				context.fireTestFinished();
			}
		}
	}

	class BeforeAndAfter extends Link {
		private final Link fNext;

		public BeforeAndAfter(Link next) {
			fNext= next;
		}

		@Override
		public void run(final Roadie context) {
			context.runProtected(JUnit4MethodRunner.this, new Runnable() {
				public void run() {
					fNext.run(context);
				}
			});
		}
	}

	class Timeout extends Link {
		private Link fNext;

		Timeout(Link next) {
			fNext= next;
		}

		@Override
		public void run(Roadie context) {
			long timeout= fTestMethod.getTimeout();
			if (timeout > 0)
				runWithActualTimeout(context, timeout, fNext);
			else
				fNext.run(context);
		}
	}

	protected void run(Roadie context) {
		chain().run(context);
	}

	private Link chain() {
		Link expectedException= new Link() {
			@Override
			public void run(Roadie context) {
				runWithExpectedExceptionCheck(context);
			}		
		};
		
		Timeout timeout= new Timeout(expectedException);
		return new Notifier(new BeforeAndAfter(timeout));
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
		ExplosiveMethod.from(fTestMethod.getMethod()).invoke(
				context.getTarget());
	}

	protected void runWithActualTimeout(final Roadie context, final long timeout, final Link next) {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				next.run(context);
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