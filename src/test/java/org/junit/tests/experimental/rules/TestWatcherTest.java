package org.junit.tests.experimental.rules;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.*;
import static org.junit.runner.JUnitCore.runClasses;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestWatcherTest {
	public static class ViolatedAssumptionTest {
		private static StringBuilder watchedLog= new StringBuilder();

		@Rule
		public TestRule watcher= new LoggingTestWatcher(watchedLog);

		@Test
		public void succeeds() {
			assumeTrue(false);
		}
	}

	@Test
	public void neitherLogSuccessNorFailedForViolatedAssumption() {
		ViolatedAssumptionTest.watchedLog= new StringBuilder();
		runClasses(ViolatedAssumptionTest.class);
		assertThat(ViolatedAssumptionTest.watchedLog.toString(),
				is("starting finished "));
	}

	public static class FailingTest {
		private static StringBuilder watchedLog= new StringBuilder();

		@Rule
		public TestRule watcher= new LoggingTestWatcher(watchedLog);

		@Test
		public void succeeds() {
			fail();
		}
	}

	@Test
	public void logFailingTest() {
		FailingTest.watchedLog= new StringBuilder();
		runClasses(FailingTest.class);
		assertThat(FailingTest.watchedLog.toString(),
				is("starting failed finished "));
	}
	
	public static class TestWatcherThrowsExceptionTest {
		@Rule
		public TestRule watcher= new TestWatcher() {
			@Override
			protected void failed(Throwable e, Description description) {
				throw new RuntimeException("watcher failure");
			}
		};

		@Test
		public void fails() {
			throw new IllegalArgumentException("test failure");
		}
	}
	
	@Test
	public void testWatcherThrowsException() {
		PrintableResult result= testResult(TestWatcherThrowsExceptionTest.class);
		assertThat(result, failureCountIs(2));
		assertThat(result, hasFailureContaining("test failure"));
		assertThat(result, hasFailureContaining("watcher failure"));
		
	}
}