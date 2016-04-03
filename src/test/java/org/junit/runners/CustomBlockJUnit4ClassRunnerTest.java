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
import org.junit.tests.mock.MockTestRunner;

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
	    MockTestRunner testRunner = new MockTestRunner();

		new CustomBlockJUnit4ClassRunner(CustomBlockJUnit4ClassRunnerTestCase.class).run(testRunner.getNotifier());
		assertEquals("tests started.", 2, testRunner.getTestStartedCount());
		assertEquals("tests failed.", 1, testRunner.getTestFailureCount());
		assertEquals("tests finished.", 2, testRunner.getTestFinishedCount());
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
}
