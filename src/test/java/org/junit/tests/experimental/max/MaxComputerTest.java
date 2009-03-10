package org.junit.tests.experimental.max;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.max.MaxHistory;
import org.junit.internal.builders.JUnit3Builder;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class MaxComputerTest {
	private static class MaxComputer extends Computer {
		// TODO (Mar 2, 2009 11:21:28 PM): configure somehow
		MaxHistory fMax= new MaxHistory(new File("MaxCore.max"));

		@Override
		protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
				throws Throwable {
			Runner junit3Runner= new JUnit3Builder().runnerForClass(testClass);
			if (junit3Runner != null)
				return junit3Runner;
			return new MaxRunner(testClass, fMax);
		}
	}

	private static class MaxRunner extends BlockJUnit4ClassRunner {
		private final MaxHistory fMax;

		// TODO (Mar 2, 2009 10:59:15 PM): this grows without bound.
		private List<FrameworkMethod> methods= new ArrayList<FrameworkMethod>();

		public MaxRunner(Class<?> klass, MaxHistory max)
				throws InitializationError {
			super(klass);
			fMax= max;
		}

		@Override
		protected void runChild(FrameworkMethod method, RunNotifier notifier) {
			methods.add(method);
		}

		@Override
		public void run(RunNotifier notifier) {
			notifier.addListener(fMax.listener());
			super.run(notifier);
			sortMethods();
			for (FrameworkMethod each : methods) {
				super.runChild(each, notifier);
			}
		}

		private void sortMethods() {
			Collections.sort(methods, new Comparator<FrameworkMethod>() {
				public int compare(FrameworkMethod o1, FrameworkMethod o2) {
					return fMax.testComparator().compare(describe(o1),
							describe(o2));
				}

				private Description describe(FrameworkMethod o1) {
					return Description.createTestDescription(o1.getMethod()
							.getDeclaringClass(), o1.getName());
				}
			});
		}
	}

	public static class TwoTests {
		@Test
		public void succeed() {
		}

		@Test
		public void dontSucceed() {
			fail();
		}
	}

	@Test
	public void twoTestsNotRunComeBackInRandomOrder() {
		Result result= new JUnitCore().run(new MaxComputer(), TwoTests.class);
		assertEquals(2, result.getRunCount());
		assertEquals(1, result.getFailureCount());
		assertEquals("dontSucceed", result.getFailures().get(0)
				.getDescription().getMethodName());
	}

	@Test
	public void failedTestFirstOnSecondRun() {
		MaxComputer computer= new MaxComputer();
		new JUnitCore().run(computer, TwoTests.class);
		JUnitCore core= new JUnitCore();
		final List<Description> testOrder= new ArrayList<Description>();
		core.addListener(new RunListener() {
			@Override
			public void testStarted(Description description) throws Exception {
				testOrder.add(description);
			}
		});
		core.run(computer, TwoTests.class);
		assertEquals("dontSucceed", testOrder.get(0).getMethodName());
		assertEquals(2, testOrder.size());
	}
	
	public static class TwoOldTests extends TestCase {
		public void testSucceed() {
		}

		public void testDontSucceed() {
			fail();
		}
	}

	@Test
	public void junit3TestsAreRunOnce() throws Exception {
		Result result= new JUnitCore().run(new MaxComputer(), TwoOldTests.class);
		assertEquals(2, result.getRunCount());
		assertEquals(1, result.getFailureCount());
		assertEquals("testDontSucceed", result.getFailures().get(0)
				.getDescription().getMethodName());
	}

	// Description succeed= Description.createTestDescription(TwoTests.class,
	// "succeed");
	// Description dontSucceed= Description.createTestDescription(
	// TwoTests.class, "dontSucceed");
	// assertTrue(things.contains(succeed));
	// assertTrue(things.contains(dontSucceed));
	// assertEquals(2, things.size());
	// }
}
