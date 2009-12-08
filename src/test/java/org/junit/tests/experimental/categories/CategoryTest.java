package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.experimental.categories.Categories.ExcludeCategory;
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
	public interface FastTests {
		// category marker
	}

	public interface SlowTests {
		// category marker
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
	
	public static class Category1 {}
	public static class Category2 {}
	
	public static class SomeAreSlow {
		@Test public void noCategory() {}
		@Category(Category1.class) @Test public void justCategory1() {}
		@Category(Category2.class) @Test public void justCategory2() {}
		@Category({Category1.class, Category2.class}) @Test public void both() {}
		@Category({Category2.class, Category1.class}) @Test public void bothReversed() {}
	}

	@RunWith(Categories.class)
	@ExcludeCategory(Category1.class)
	@SuiteClasses( { SomeAreSlow.class })
	public static class SomeAreSlowSuite {
	}
	
	@Test
	public void testCountOnAWithoutSlowTests() {
		Result result= JUnitCore.runClasses(SomeAreSlowSuite.class);
		assertThat(testResult(SomeAreSlowSuite.class), isSuccessful());
		assertEquals(2, result.getRunCount());
		assertTrue(result.wasSuccessful());
	}

	@RunWith(Categories.class)
	@ExcludeCategory(Category1.class)
	@IncludeCategory(Category2.class)
	@SuiteClasses( { SomeAreSlow.class })
	public static class IncludeAndExcludeSuite {
	}
	
	@Test
	public void testsThatAreBothIncludedAndExcludedAreExcluded() {
		Result result= JUnitCore.runClasses(IncludeAndExcludeSuite.class);
		assertThat(testResult(SomeAreSlowSuite.class), isSuccessful());
		assertEquals(1, result.getRunCount());
		assertTrue(result.wasSuccessful());
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
		assertEquals(2, result.getRunCount());
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
		CategoryFilter filter= CategoryFilter.include(SlowTests.class);
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
	
	public static class ClassAsCategory {
		
	}
	
	public static class OneMoreTest {
		@Category(ClassAsCategory.class) @Test public void a() {}
	}

	@RunWith(Categories.class)
	@IncludeCategory(ClassAsCategory.class)
	@SuiteClasses( { OneMoreTest.class })
	public static class RunClassAsCategory {
	}
	
	@Test public void classesCanBeCategories() {
		assertThat(testResult(RunClassAsCategory.class), isSuccessful());
	}
}
