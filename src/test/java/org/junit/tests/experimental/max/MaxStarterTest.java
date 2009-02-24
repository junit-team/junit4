package org.junit.tests.experimental.max;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.max.CouldNotReadCoreException;
import org.junit.experimental.max.MaxCore;
import org.junit.internal.requests.SortingRequest;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.tests.AllTests;

public class MaxStarterTest {
	private MaxCore fMax;

	@Before
	public void createMax() {
		fMax= MaxCore.createFresh();
	}

	@After
	public void forgetMax() {
		fMax.forget();
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
		Request request= Request.aClass(TwoTests.class);
		List<Description> things= fMax.sortedLeavesForTest(request);
		Description succeed= Description.createTestDescription(TwoTests.class,
				"succeed");
		Description dontSucceed= Description.createTestDescription(
				TwoTests.class, "dontSucceed");
		assertTrue(things.contains(succeed));
		assertTrue(things.contains(dontSucceed));
		assertEquals(2, things.size());
	}

	@Test
	public void preferNewTests() {
		Request one= Request.method(TwoTests.class, "succeed");
		fMax.run(one);
		Request two= Request.aClass(TwoTests.class);
		List<Description> things= fMax.sortedLeavesForTest(two);
		Description dontSucceed= Description.createTestDescription(
				TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, things.get(0));
		assertEquals(2, things.size());
	}

	// This covers a seemingly-unlikely case, where you had a test that failed
	// on the
	// last run and you also introduced new tests. In such a case it pretty much
	// doesn't matter
	// which order they run, you just want them both to be early in the sequence
	@Test
	public void preferNewTestsOverTestsThatFailed() {
		Request one= Request.method(TwoTests.class, "dontSucceed");
		fMax.run(one);
		Request two= Request.aClass(TwoTests.class);
		List<Description> things= fMax.sortedLeavesForTest(two);
		Description succeed= Description.createTestDescription(TwoTests.class,
				"succeed");
		assertEquals(succeed, things.get(0));
		assertEquals(2, things.size());
	}

	@Test
	public void preferRecentlyFailed() {
		Request request= Request.aClass(TwoTests.class);
		fMax.run(request);
		List<Description> tests= fMax.sortedLeavesForTest(request);
		Description dontSucceed= Description.createTestDescription(
				TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, tests.get(0));
	}

	@Test
	public void sortTestsInMultipleClasses() {
		Request request= Request.classes(Computer.serial(), TwoTests.class,
				TwoTests.class);
		fMax.run(request);
		List<Description> tests= fMax.sortedLeavesForTest(request);
		Description dontSucceed= Description.createTestDescription(
				TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, tests.get(0));
		assertEquals(dontSucceed, tests.get(1));
	}

	public static class TwoUnEqualTests {
		@Test
		public void slow() throws InterruptedException {
			Thread.sleep(100);
		}

		@Test
		public void fast() {
		}
	}

	@Test
	public void preferFast() {
		Request request= Request.aClass(TwoUnEqualTests.class);
		fMax.run(request);
		Description thing= fMax.sortedLeavesForTest(request).get(1);
		assertEquals(Description.createTestDescription(TwoUnEqualTests.class,
				"slow"), thing);
		// TODO (Nov 18, 2008 2:03:06 PM): flaky?
	}

	@Test
	public void remember() throws CouldNotReadCoreException {
		Request request= Request.aClass(TwoUnEqualTests.class);
		fMax.run(request);
		MaxCore reincarnation= MaxCore.forFolder(fMax.getFolder());
		try {
			Description thing= reincarnation.sortedLeavesForTest(request)
					.get(1);
			assertEquals(Description.createTestDescription(
					TwoUnEqualTests.class, "slow"), thing);
		} finally {
			reincarnation.forget();
		}
	}

	@Test
	public void listenersAreCalledCorrectlyInTheFaceOfFailures()
			throws Exception {
		JUnitCore core= new JUnitCore();
		final List<Failure> failures= new ArrayList<Failure>();
		core.addListener(new RunListener() {
			@Override
			public void testRunFinished(Result result) throws Exception {
				failures.addAll(result.getFailures());
			}
		});
		fMax.run(Request.aClass(TwoTests.class), core);
		assertEquals(1, failures.size());
	}

	@Test
	public void testsAreOnlyIncludedOnceWhenExpandingForSorting()
			throws Exception {
		Result result= fMax.run(Request.aClass(TwoTests.class));
		assertEquals(2, result.getRunCount());
	}

	public static class TwoOldTests extends TestCase {
		public void testOne() {
		}

		public void testTwo() {
		}
	}

	@Test
	public void junit3TestsAreRunOnce() throws Exception {
		Result result= fMax.run(Request.aClass(TwoOldTests.class),
				new JUnitCore());
		assertEquals(2, result.getRunCount());
	}

	@Test
	public void saffSqueezeExample() throws Exception {
		final Description method= Description.createTestDescription(
				TwoOldTests.class, "testOne");
		Filter filter= Filter.matchDescription(method);
		JUnit38ClassRunner child= new JUnit38ClassRunner(TwoOldTests.class);
		child.filter(filter);
		assertEquals(1, child.testCount());
	}

	@Test
	public void testCountsMatchUp() {
		JUnitCore core= new JUnitCore();
		Request filtered= Request.aClass(AllTests.class).filterWith(
				new Filter() {
					@Override
					public boolean shouldRun(Description description) {
						return !description.toString().contains("Max");
					}

					@Override
					public String describe() {
						return "Avoid infinite recursion";
					}
				});
		int maxCount= fMax.run(filtered, core).getRunCount();
		int coreCount= core.run(filtered).getRunCount();
		assertEquals(coreCount, maxCount);
	}

	@Test
	public void testCountsStandUpToFiltration() {
		// TODO (Nov 18, 2008 4:42:43 PM): DUP above
		Class<AllTests> testClass= AllTests.class;
		assertFilterLeavesTestUnscathed(testClass);
	}

	private void assertFilterLeavesTestUnscathed(Class<?> testClass) {
		Request oneClass= Request.aClass(testClass);
		Request filtered= oneClass.filterWith(new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				return true;
			}

			@Override
			public String describe() {
				return "Everything";
			}
		});

		int filterCount= filtered.getRunner().testCount();
		int coreCount= oneClass.getRunner().testCount();
		assertEquals("Counts match up in " + testClass, coreCount, filterCount);
	}

	private static class MalformedJUnit38Test {
		private MalformedJUnit38Test() {
		}

		public void testSucceeds() {
		}
	}

	@Test
	public void maxShouldSkipMalformedJUnit38Classes() {
		Request request= Request.aClass(MalformedJUnit38Test.class);
		fMax.run(request);
	}

	public static class MalformedJUnit38TestMethod extends TestCase {
		private void testNothing() {
		}
	}

	String fMessage= null;

	@Test
	public void correctErrorFromMalformedTestSqueeze() {
		Request request= Request.aClass(MalformedJUnit38TestMethod.class);
		assertFalse(request instanceof SortingRequest);
		JUnit38ClassRunner runner= (JUnit38ClassRunner) request.getRunner();
		junit.framework.Test test= runner.getTest();
		assertThat(test, IsInstanceOf.instanceOf(TestSuite.class));
		Description description= JUnit38ClassRunner.makeDescription(test);
		assertThat(description.toString(),
				containsString("MalformedJUnit38TestMethod"));
		// assertFalse(description.getChildren().isEmpty());
		// assertThat(description.getChildren().size(), is(1));
		// assertThat(description.getChildren().get(0).toString(),
		// containsString("MalformedJUnit38TestMethod"));
	}

	@Test
	public void correctErrorFromMalformedTestSqueeze2() {
		Request request= Request.aClass(MalformedJUnit38TestMethod.class);
		assertFalse(request instanceof SortingRequest);
		List<Description> leaves= fMax.findLeaves(request);
		Collections.sort(leaves, fMax.testComparator());
		Description each= leaves.get(0);
		assertFalse(each.toString().equals("TestSuite with 0 tests"));
		assertEquals(MalformedJUnit38TestMethod.class.getName(), each
				.toString());
		String name= each.getClassName();
		assertNotNull(name);
	}

	@Test
	public void correctErrorFromMalformedTest() {
		Request request= Request.aClass(MalformedJUnit38TestMethod.class);
		JUnitCore core= new JUnitCore();
		Request sorted= fMax.sortRequest(request);
		Runner runner= sorted.getRunner();
		Result result= core.run(runner);
		Failure failure= result.getFailures().get(0);

		assertThat(failure.toString(),
				containsString("MalformedJUnit38TestMethod"));
		assertThat(failure.toString(), containsString("testNothing"));
		assertThat(failure.toString(), containsString("isn't public"));
	}

	public static class HalfMalformedJUnit38TestMethod extends TestCase {
		public void testSomething() {
		}

		private void testNothing() {
		}
	}

	@Test
	public void halfMalformed() {
		assertThat(JUnitCore.runClasses(HalfMalformedJUnit38TestMethod.class)
				.getFailureCount(), is(1));
	}
}
