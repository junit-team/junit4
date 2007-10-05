package org.junit.internal.runners;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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

	// TODO document and promote Link and its subclasses to top-level classes
	/*
	 * One thing we've noticed about running test methods is that it has gotten more complicated over time.
	 * The functionality has become the composition of some very different behavior--catching exceptions,
	 * notifying listeners, setting up and tearing down. Customer runners compound this diversity.
	 * 
	 * At one time we had a nested sequence of methods to represent this composition, but it was hard
	 * to change and hard to override. Instead, we now have a chain of decorators (hence the base
	 * class Link), each of which handles one aspect of running test methods. Custom runners can
	 * create specialized behavior by inserting their own links into the chain, reusing existing links
	 * or not as appropriate..
	 */
	public abstract class Link {
		public abstract void run(Roadie context);
	}

	public class Notifier extends Link {
		private final Link fNext;

		public Notifier(Link link) {
			fNext= link;
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

	public class BeforeAndAfter extends Link {
		private final Anchor fNext;
		
		public BeforeAndAfter(Anchor next) {
			fNext= next;
		}

		@Override
		public void run(final Roadie context) {
			context.runProtected(JUnit4MethodRunner.this, new Runnable() {
				public void run() {
					// TODO: (Oct 5, 2007 11:56:12 AM) DUP with Theory

					try {
						fNext.run(context);
					} catch (Throwable e) {
						context.addFailure(e);
					}
				}			
			});
		}
	}
	
	public Anchor timeout(Anchor next) {
		return fTestMethod.getTimeout() > 0
			? new Timeout(next)
			: next;
	}

	class Timeout extends Anchor {
		private Anchor fNext;

		Timeout(Anchor next) {
			fNext= next;
		}

		@Override
		public void run(final Roadie context) {
			long timeout= fTestMethod.getTimeout();
			ExecutorService service= Executors.newSingleThreadExecutor();
			Callable<Object> callable= new Callable<Object>() {
				public Object call() throws Exception {
					try {
						fNext.run(context);
					} catch (Exception e) {
						throw e;
					} catch (Error e) {
						throw e;
					} catch (Throwable e) {
						// TODO: (Oct 5, 2007 11:27:11 AM) Now what?
					}
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
				result.get(0, TimeUnit.MILLISECONDS); // throws the exception if one occurred during the invocation
			} catch (TimeoutException e) {
				context.addFailure(new Exception(String.format(
						"test timed out after %d milliseconds", timeout)));
			} catch (ExecutionException e) {
				context.addFailure(e.getCause());
			} catch (Exception e) {
				context.addFailure(e);
			}
		}
	}

	public Anchor handleExceptions(Anchor next) {
		return fTestMethod.expectsException()
			? new ExpectedException(next)
			: new NoExpectedException(next);
	}
	
	public class ExpectedException extends Anchor {
		Anchor fNext;
		public ExpectedException(Anchor next) {
			fNext= next;
		}
		
		@Override
		public void run(Roadie context) {
			try {
				fNext.run(context);
				context.addFailure(new AssertionError("Expected exception: "
						+ fTestMethod.getExpectedException().getName()));
			} catch (AssumptionViolatedException e) {
				// Do nothing
			} catch (Throwable e) {
				if (fTestMethod.isUnexpected(e)) {
					String message= "Unexpected exception, expected<"
								+ fTestMethod.getExpectedException().getName() + "> but was<"
								+ e.getClass().getName() + ">";
					context.addFailure(new Exception(message, e));
				}
			}
		}
	}

	public class NoExpectedException extends Anchor {
		Anchor fNext;
		public NoExpectedException(Anchor next) {
			fNext= next;
		}
		
		@Override
		public void run(Roadie context) throws Throwable {
			try {
				fNext.run(context);
			} catch (AssumptionViolatedException e) {
				// Do nothing
			}
		}
	}

	// TODO: (Oct 5, 2007 11:41:41 AM) rename method, extract class

	protected Anchor anchor() {
		return new Anchor(){
			@Override
			public void run(Roadie context) throws Throwable {
				ExplosiveMethod.from(fTestMethod.getMethod()).invoke(
						context.getTarget());
			}
		};
	}
	
	public static abstract class Anchor {
		public abstract void run(Roadie context) throws Throwable;
	}
	
	protected void run(Roadie context) {
		chain().run(context);
	}

	protected Link chain() {
		// TODO: (Oct 5, 2007 11:09:00 AM) Rename Anchor and Link

		Anchor anchor= anchor();
		Anchor next= handleExceptions(anchor);
		next= timeout(next);
		Link link= new BeforeAndAfter(next);
		return new Notifier(link);
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