package org.junit.tests.experimental;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.Test;
import org.junit.experimental.interceptor.Interceptor;
import org.junit.experimental.interceptor.Interceptors;
import org.junit.experimental.interceptor.StatementInterceptor;
import org.junit.experimental.interceptor.TestWatchman;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class InterceptorTest {
	private static boolean wasRun;

	@RunWith(Interceptors.class)
	public static class ExampleTest {
		@Interceptor
		public StatementInterceptor example= new StatementInterceptor() {
			// TODO (Apr 28, 2009 10:31:18 PM): much better error if
			// @Interceptor
			// annotates a non-public field.
			public Statement intercept(final Statement base,
					FrameworkMethod method) {
				return new Statement() {
					@Override
					public void evaluate() throws Throwable {
						wasRun= true;
						base.evaluate();
					};
				};
			}
		};

		@Test
		public void nothing() {

		}
	}

	@Test
	public void interceptorIsIntroducedAndEvaluated() {
		wasRun= false;
		JUnitCore.runClasses(ExampleTest.class);
		assertTrue(wasRun);
	}

	private static int runCount;

	@RunWith(Interceptors.class)
	public static class MultipleInterceptorTest {
		private static class Incrementor implements StatementInterceptor {
			public Statement intercept(final Statement base,
					FrameworkMethod method) {
				return new Statement() {
					@Override
					public void evaluate() throws Throwable {
						runCount++;
						base.evaluate();
					};
				};
			}
		}

		@Interceptor
		public StatementInterceptor interceptor1= new Incrementor();

		@Interceptor
		public StatementInterceptor interceptor2= new Incrementor();

		@Test
		public void nothing() {

		}
	}

	@Test
	public void multipleInterceptorsAreRun() {
		runCount= 0;
		JUnitCore.runClasses(MultipleInterceptorTest.class);
		assertEquals(2, runCount);
	}

	@RunWith(Interceptors.class)
	public static class NoInterceptorsTest {
		public int x;

		@Test
		public void nothing() {

		}
	}

	@Test
	public void ignoreNonInterceptors() {
		Result result= JUnitCore.runClasses(NoInterceptorsTest.class);
		assertEquals(0, result.getFailureCount());
	}

	private static String log;

	@RunWith(Interceptors.class)
	public static class OnFailureTest {
		@Interceptor
		public StatementInterceptor watchman= new TestWatchman() {
			// TODO (Apr 28, 2009 10:50:47 PM): is this right? Is
			// FrameworkMethod too powerful?
			@Override
			public void failed(Throwable e, FrameworkMethod method) {
				log+= method.getName() + " " + e.getClass().getSimpleName();
			}
		};

		@Test
		public void nothing() {
			fail();
		}
	}

	@Test
	public void onFailure() {
		log= "";
		Result result= JUnitCore.runClasses(OnFailureTest.class);
		assertEquals("nothing AssertionError", log);
		assertEquals(1, result.getFailureCount());
	}

	@RunWith(Interceptors.class)
	public static class WatchmanTest {
		private static String watchedLog;

		@Interceptor
		public StatementInterceptor watchman= new TestWatchman() {
			@Override
			public void failed(Throwable e, FrameworkMethod method) {
				watchedLog+= method.getName() + " "
						+ e.getClass().getSimpleName() + "\n";
			}

			@Override
			public void succeeded(FrameworkMethod method) {
				watchedLog+= method.getName() + " " + "success!\n";
			}
		};

		@Test
		public void fails() {
			fail();
		}

		@Test
		public void succeeds() {
		}
	}

	@Test
	public void succeeded() {
		WatchmanTest.watchedLog= "";
		JUnitCore.runClasses(WatchmanTest.class);
		assertThat(WatchmanTest.watchedLog, containsString("fails AssertionError"));
		assertThat(WatchmanTest.watchedLog, containsString("succeeds success!"));
	}
}
