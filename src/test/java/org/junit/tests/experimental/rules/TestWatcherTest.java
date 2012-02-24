package org.junit.tests.experimental.rules;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.runner.JUnitCore.runClasses;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

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
}