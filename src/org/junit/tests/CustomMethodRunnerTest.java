package org.junit.tests;

import static org.junit.Assert.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.TestIntrospector;
import org.junit.internal.runners.TestMethod;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.MethodRunner;
import org.junit.runners.Replaces;
import org.junit.runners.RunMethodWith;

public class CustomMethodRunnerTest {
	@RunMethodWith(CustomMethodRunner.class)
	@Replaces(Test.class)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyIgnore {

	}

	public static class CustomMethodRunner implements MethodRunner {
		public void run(TestMethod method, RunNotifier notifier) {
			notifier.fireTestStarted(method.getDescription());
			notifier.fireTestIgnored(method.getDescription());
			notifier.fireTestFinished(method.getDescription());
		}
	}

	public static class UsesCustomMethodRunner {
		@MyIgnore
		public void thisIsIgnored() {
			Assert.fail();
		}
	}

	@Test
	public void countCustomTests() {
		assertEquals(1, new TestIntrospector(UsesCustomMethodRunner.class)
				.getTestMethods(Test.class).size());
	}

	@Test
	public void customMethodRunner() {
		Result result= JUnitCore.runClasses(UsesCustomMethodRunner.class);
		assertEquals(1, result.getIgnoreCount());
		assertEquals(0, result.getFailureCount());
	}

	public static class EvilCustomMethodRunner implements MethodRunner {
		public EvilCustomMethodRunner() {
			throw new RuntimeException();
		}

		public void run(TestMethod method, RunNotifier notifier) {
			// do nothing
		}
	}

	public static class UsesEvilCustomMethodRunner {
		@RunMethodWith(EvilCustomMethodRunner.class)
		@Test
		public void thisShouldNeverRun() {
		}
	}

	@Test
	public void problemInstantiatingCustomRunner() {
		Result result= JUnitCore.runClasses(UsesEvilCustomMethodRunner.class);
		Failure failure= result.getFailures().get(0);
		assertTrue(failure.getMessage().contains(
				"Exception creating custom method runner"));
	}

	public static class PointlessCustomMethodRunner implements MethodRunner {
		public void run(TestMethod method, RunNotifier notifier) {
			new TestMethodRunner(method.getTest(), method.getJavaMethod(),
					notifier, method.getDescription()).run();
		}
	}

	public static class UsesPointlessCustomMethodRunner {
		@RunMethodWith(PointlessCustomMethodRunner.class)
		@Test
		public void thisShouldRun() {
			fail("Something happened!");
		}
	}

	@Test
	public void allInformationIsPassedToMethodRunner() {
		Result result= JUnitCore
				.runClasses(UsesPointlessCustomMethodRunner.class);
		Failure failure= result.getFailures().get(0);
		assertEquals("Something happened!", failure.getMessage());
	}

	@Test
	public void allInformationIsPassedToNotifier() {
		RunListener listener= new RunListener() {
			@Override
			public void testStarted(Description description) throws Exception {
				assertEquals(
						Description.createTestDescription(
								UsesPointlessCustomMethodRunner.class,
								"thisShouldRun"), description);
			}
		};
		JUnitCore core= new JUnitCore();
		core.addListener(listener);
		core.run(UsesPointlessCustomMethodRunner.class);
	}

}
