package org.junit.runners;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Tests that verify proper behavior for custom runners that extend
 * {@link BlockJUnit4ClassRunner}.
 *
 * @author Sam Brannen
 * @since 4.13
 */
public class CustomBlockJUnit4ClassRunnerTest {

	@Test
	public void exceptionsFromMethodBlockMustNotResultInUnrootedTests() throws Exception {
		TrackingRunListener listener = new TrackingRunListener();
		RunNotifier notifier = new RunNotifier();
		notifier.addListener(listener);

		new CustomBlockJUnit4ClassRunner(CustomBlockJUnit4ClassRunnerTestCase.class).run(notifier);
		assertEquals("tests started.", 2, listener.testStartedCount.get());
		assertEquals("tests failed.", 1, listener.testFailureCount.get());
		assertEquals("tests finished.", 2, listener.testFinishedCount.get());
	}


	public static class CustomBlockJUnit4ClassRunnerTestCase {
		@Test public void shouldPass() { /* no-op */ }
		@Test public void throwException() { /* no-op */ }
	}

	/**
	 * Custom extension of {@link BlockJUnit4ClassRunner} that always throws
	 * an exception from the {@code methodBlock()} if a test method is named
	 * exactly {@code "throwException"}.
	 */
	private static class CustomBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {

		CustomBlockJUnit4ClassRunner(Class<?> testClass) throws InitializationError {
			super(testClass);
		}

		@Override
		protected Statement methodBlock(FrameworkMethod method) {
			if ("throwException".equals(method.getName())) {
				throw new RuntimeException("throwException() test method invoked");
			}
			return super.methodBlock(method);
		}
	}

	/**
	 * Simple {@link RunListener} that tracks the number of times that
	 * certain callbacks are invoked.
	 */
	private static class TrackingRunListener extends RunListener {

		final AtomicInteger testStartedCount = new AtomicInteger();
		final AtomicInteger testFailureCount = new AtomicInteger();
		final AtomicInteger testFinishedCount = new AtomicInteger();


		@Override
		public void testStarted(Description description) throws Exception {
			testStartedCount.incrementAndGet();
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			testFailureCount.incrementAndGet();
		}

		@Override
		public void testFinished(Description description) throws Exception {
			testFinishedCount.incrementAndGet();
		}
	}

}
