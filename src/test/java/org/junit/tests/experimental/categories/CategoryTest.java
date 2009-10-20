package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.CategoryType;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;

public class CategoryTest {
	public interface FastTests extends CategoryType {

	}

	public interface SlowTests extends CategoryType {

	}

	public static class A {
		@Test
		public void a() {
			fail();
		}

		@Category(SlowTests.class)
		@Test
		public void b() {
		}
	}

	@Category(SlowTests.class)
	public static class B {
		@Test
		public void c() {

		}
	}

	public static class C {
		@Test
		public void d() {
			fail();
		}
	}

	@RunWith(Categories.class)
	@IncludeCategory(SlowTests.class)
	@SuiteClasses( { A.class, B.class, C.class })
	public static class SlowTestSuite {
	}

	@RunWith(Categories.class)
	@IncludeCategory(SlowTests.class)
	@SuiteClasses( { A.class })
	public static class JustA {
	}

	@Test
	public void testCountOnJustA() {
		assertThat(testResult(JustA.class), isSuccessful());
	}

	@Test
	public void testCount() {
		assertThat(testResult(SlowTestSuite.class), isSuccessful());
	}

	@RunWith(Suite.class)
	@SuiteClasses( { A.class, B.class, C.class })
	public static class TestSuiteWithNoCategories {
	}

	@Test
	public void testCountWithExplicitFilter() throws Throwable {
		CategoryFilter include= CategoryFilter.include(SlowTests.class);
		Request baseRequest= Request.aClass(TestSuiteWithNoCategories.class);
		Result result= new JUnitCore().run(baseRequest.filterWith(include));
		assertTrue(result.wasSuccessful());
	}

	@Test
	public void categoryFilterLeavesOnlyMatchingMethods()
			throws InitializationError, NoTestsRemainException {
		CategoryFilter filter= CategoryFilter.include(SlowTests.class);
		BlockJUnit4ClassRunner runner= new BlockJUnit4ClassRunner(A.class);
		filter.apply(runner);
		assertEquals(1, runner.testCount());
	}

	public static class OneFastOneSlow {
		@Category(FastTests.class)
		@Test
		public void a() {

		}

		@Category(SlowTests.class)
		@Test
		public void b() {

		}
	}

	@Test
	public void categoryFilterRejectsIncompatibleCategory()
			throws InitializationError, NoTestsRemainException {
		CategoryFilter filter= CategoryFilter.include(SlowTests.class);
		BlockJUnit4ClassRunner runner= new BlockJUnit4ClassRunner(
				OneFastOneSlow.class);
		filter.apply(runner);
		assertEquals(1, runner.testCount());
	}

	public static class OneFast {
		@Category(FastTests.class)
		@Test
		public void a() {

		}
	}

	@RunWith(Categories.class)
	@IncludeCategory(SlowTests.class)
	@SuiteClasses( { OneFast.class })
	public static class OneFastSuite {
	}

	@Test
	public void ifNoTestsToRunUseErrorRunner() {
		Result result= JUnitCore.runClasses(OneFastSuite.class);
		assertEquals(1, result.getRunCount());
		assertEquals(1, result.getFailureCount());
	}

	@Test
	public void describeACategoryFilter() {
		CategoryFilter filter= new CategoryFilter(SlowTests.class);
		assertEquals("category " + SlowTests.class, filter.describe());
	}
	
	public static class OneThatIsBothFastAndSlow {
		@Category({FastTests.class, SlowTests.class})
		@Test
		public void a() {

		}
	}

	@RunWith(Categories.class)
	@IncludeCategory(SlowTests.class)
	@SuiteClasses( { OneThatIsBothFastAndSlow.class })
	public static class ChooseSlowFromBoth {
	}
	
	@Test public void runMethodWithTwoCategories() {
		assertThat(testResult(ChooseSlowFromBoth.class), isSuccessful());
	}
	
	public interface VerySlowTests extends SlowTests {
		
	}
	
	public static class OneVerySlowTest {
		@Category(VerySlowTests.class)
		@Test
		public void a() {

		}
	}

	@RunWith(Categories.class)
	@IncludeCategory(SlowTests.class)
	@SuiteClasses( { OneVerySlowTest.class })
	public static class RunSlowFromVerySlow {
	}
	
	@Test public void subclassesOfIncludedCategoriesAreRun() {
		assertThat(testResult(RunSlowFromVerySlow.class), isSuccessful());
	}
}
