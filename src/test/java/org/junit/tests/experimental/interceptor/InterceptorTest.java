package org.junit.tests.experimental.interceptor;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.interceptor.Rule;
import org.junit.experimental.interceptor.MethodRule;
import org.junit.experimental.interceptor.TestName;
import org.junit.experimental.interceptor.TestWatchman;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class InterceptorTest {
	private static boolean wasRun;

	public static class ExampleTest {
		@Rule
		public MethodRule example= new MethodRule() {
			public Statement apply(final Statement base,
					FrameworkMethod method, Object target) {
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

	public static class MultipleInterceptorTest {
		private static class Increment implements MethodRule {
			public Statement apply(final Statement base,
					FrameworkMethod method, Object target) {
				return new Statement() {
					@Override
					public void evaluate() throws Throwable {
						runCount++;
						base.evaluate();
					};
				};
			}
		}

		@Rule
		public MethodRule incrementor1= new Increment();

		@Rule
		public MethodRule incrementor2= new Increment();

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

	public static class OnFailureTest {
		@Rule
		public MethodRule watchman= new TestWatchman() {
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

	public static class WatchmanTest {
		private static String watchedLog;

		@Rule
		public MethodRule watchman= new TestWatchman() {
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

	public static class BeforesAndAfters {
		private static String watchedLog;

		@Before public void before() {
			watchedLog+= "before ";
		}
		
		@Rule
		public MethodRule watchman= new TestWatchman() {
			@Override
			public void starting(FrameworkMethod method) {
				watchedLog+= "starting ";
			}
			
			@Override
			public void finished(FrameworkMethod method) {
				watchedLog+= "finished ";
			}
			
			@Override
			public void succeeded(FrameworkMethod method) {
				watchedLog+= "succeeded ";
			}
		};
		
		@After public void after() {
			watchedLog+= "after ";
		}

		@Test
		public void succeeds() {
			watchedLog+= "test ";
		}
	}

	@Test
	public void beforesAndAfters() {
		BeforesAndAfters.watchedLog= "";
		JUnitCore.runClasses(BeforesAndAfters.class);
		assertThat(BeforesAndAfters.watchedLog, is("before starting test succeeded finished after "));
	}
	
	public static class WrongTypedField {
		@Rule public int x = 5;
		@Test public void foo() {}
	}
	
	@Test public void validateWrongTypedField() {
		assertThat(testResult(WrongTypedField.class), 
				hasSingleFailureContaining("must implement StatementInterceptor"));
	}
	
	public static class SonOfWrongTypedField extends WrongTypedField {
		
	}

	@Test public void validateWrongTypedFieldInSuperclass() {
		assertThat(testResult(SonOfWrongTypedField.class), 
				hasSingleFailureContaining("must implement StatementInterceptor"));
	}

	public static class PrivateInterceptor {
		@SuppressWarnings("unused")
		@Rule private MethodRule interceptor = new TestName();
		@Test public void foo() {}
	}
	
	@Test public void validatePrivateInterceptor() {
		assertThat(testResult(PrivateInterceptor.class), 
				hasSingleFailureContaining("must be public"));
	}
}
