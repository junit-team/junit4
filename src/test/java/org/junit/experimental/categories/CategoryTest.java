package org.junit.experimental.categories;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
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

    public interface ReallySlowTests {
        // category marker
    }

    public static class OneOfEach {

        @Category(FastTests.class)
        @Test
        public void a() {
        }

        @Category(SlowTests.class)
        @Test
        public void b() {
        }

        @Category(ReallySlowTests.class)
        @Test
        public void c() {
        }
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
    @SuiteClasses({A.class, B.class, C.class})
    public static class SlowTestSuite {
    }

    @RunWith(Categories.class)
    @IncludeCategory(SlowTests.class)
    @SuiteClasses({A.class})
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

    public static class Category1 {
    }

    public static class Category2 {
    }

    public static class SomeAreSlow {
        @Test
        public void noCategory() {
        }

        @Category(Category1.class)
        @Test
        public void justCategory1() {
        }

        @Category(Category2.class)
        @Test
        public void justCategory2() {
        }

        @Category({Category1.class, Category2.class})
        @Test
        public void both() {
        }

        @Category({Category2.class, Category1.class})
        @Test
        public void bothReversed() {
        }
    }

    @RunWith(Categories.class)
    @ExcludeCategory(Category1.class)
    @SuiteClasses({SomeAreSlow.class})
    public static class SomeAreSlowSuite {
    }

    @Test
    public void testCountOnAWithoutSlowTests() {
        Result result = JUnitCore.runClasses(SomeAreSlowSuite.class);
        assertThat(testResult(SomeAreSlowSuite.class), isSuccessful());
        assertEquals(2, result.getRunCount());
        assertTrue(result.wasSuccessful());
    }

    @RunWith(Categories.class)
    @ExcludeCategory(Category1.class)
    @IncludeCategory(Category2.class)
    @SuiteClasses({SomeAreSlow.class})
    public static class IncludeAndExcludeSuite {
    }

    @Test
    public void testsThatAreBothIncludedAndExcludedAreExcluded() {
        Result result = JUnitCore.runClasses(IncludeAndExcludeSuite.class);
        assertEquals(1, result.getRunCount());
        assertTrue(result.wasSuccessful());
    }

    @RunWith(Suite.class)
    @SuiteClasses({A.class, B.class, C.class})
    public static class TestSuiteWithNoCategories {
    }

    @Test
    public void testCountWithExplicitIncludeFilter() throws Throwable {
        CategoryFilter include = CategoryFilter.include(SlowTests.class);
        Request baseRequest = Request.aClass(TestSuiteWithNoCategories.class);
        Result result = new JUnitCore().run(baseRequest.filterWith(include));
        assertTrue(result.wasSuccessful());
        assertEquals(2, result.getRunCount());
    }

    @Test
    public void testCountWithExplicitExcludeFilter() throws Throwable {
        CategoryFilter include = CategoryFilter.exclude(SlowTests.class);
        Request baseRequest = Request.aClass(TestSuiteWithNoCategories.class);
        Result result = new JUnitCore().run(baseRequest.filterWith(include));
        assertEquals(2, result.getFailureCount());
        assertEquals(2, result.getRunCount());
    }

    @Test
    public void testCountWithExplicitExcludeFilter_usingConstructor() throws Throwable {
        CategoryFilter include = new CategoryFilter(null, SlowTests.class);
        Request baseRequest = Request.aClass(TestSuiteWithNoCategories.class);
        Result result = new JUnitCore().run(baseRequest.filterWith(include));
        assertEquals(2, result.getFailureCount());
        assertEquals(2, result.getRunCount());
    }

    @Test
    public void categoryFilterLeavesOnlyMatchingMethods()
            throws InitializationError, NoTestsRemainException {
        CategoryFilter filter = CategoryFilter.include(SlowTests.class);
        BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(A.class);
        filter.apply(runner);
        assertEquals(1, runner.testCount());
    }

    @Test
    public void categoryFilterLeavesOnlyMatchingMethods_usingConstructor()
            throws InitializationError, NoTestsRemainException {
        CategoryFilter filter = new CategoryFilter(SlowTests.class, null);
        BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(A.class);
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
        CategoryFilter filter = CategoryFilter.include(SlowTests.class);
        BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(
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
    @SuiteClasses({OneFast.class})
    public static class OneFastSuite {
    }

    @Test
    public void ifNoTestsToRunUseErrorRunner() {
        Result result = JUnitCore.runClasses(OneFastSuite.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertFalse(result.wasSuccessful());
    }

    @Test
    public void describeACategoryFilter() {
        CategoryFilter filter = CategoryFilter.include(SlowTests.class);
        assertEquals("categories [" + SlowTests.class + "]", filter.describe());
    }

    @Test
    public void describeMultipleCategoryFilter() {
        CategoryFilter filter= CategoryFilter.include(FastTests.class, SlowTests.class);
        String d1= format("categories [%s, %s]", FastTests.class, SlowTests.class);
        String d2= format("categories [%s, %s]", SlowTests.class, FastTests.class);
        assertThat(filter.describe(), is(anyOf(equalTo(d1), equalTo(d2))));
    }


    public static class OneThatIsBothFastAndSlow {
        @Category({FastTests.class, SlowTests.class})
        @Test
        public void a() {

        }
    }

    @RunWith(Categories.class)
    @IncludeCategory(SlowTests.class)
    @SuiteClasses({OneThatIsBothFastAndSlow.class})
    public static class ChooseSlowFromBoth {
    }

    @Test
    public void runMethodWithTwoCategories() {
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
    @SuiteClasses({OneVerySlowTest.class})
    public static class RunSlowFromVerySlow {
    }

    @Test
    public void subclassesOfIncludedCategoriesAreRun() {
        assertThat(testResult(RunSlowFromVerySlow.class), isSuccessful());
    }

    public interface MultiA {
    }

    public interface MultiB {
    }

    public interface MultiC {
    }

    @RunWith(Categories.class)
    @IncludeCategory(value= {MultiA.class, MultiB.class}, matchAny= false)
    @SuiteClasses(AllIncludedMustMatched.class)
    public static class AllIncludedMustBeMatchedSuite {
    }

    public static class AllIncludedMustMatched {
        @Test
        @Category({MultiA.class, MultiB.class})
        public void a() {
        }

        @Test
        @Category(MultiB.class)
        public void b() {
            fail("When multiple categories are included in a Suite, " +
                    "@Test method must match all include categories");
        }
    }

    @Test
    public void allIncludedSuiteCategoriesMustBeMatched() {
        Result result= JUnitCore.runClasses(AllIncludedMustBeMatchedSuite.class);
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
    }

    @RunWith(Categories.class)
    @IncludeCategory({MultiA.class, MultiB.class})
    @ExcludeCategory(MultiC.class)
    @SuiteClasses(MultipleIncludesAndExcludeOnMethod.class)
    public static class MultiIncludeWithExcludeCategorySuite {
    }

    public static class MultipleIncludesAndExcludeOnMethod {
        @Test
        @Category({MultiA.class, MultiB.class})
        public void a() {
        }

        @Test
        @Category({ MultiA.class, MultiB.class, MultiC.class })
        public void b() {
            fail("When multiple categories are included and excluded in a Suite, " +
                    "@Test method must match all include categories and contain non of the excluded");
        }
    }

    @Test
    public void anyMethodWithExcludedCategoryWillBeExcluded() {
        Result result= JUnitCore.runClasses(MultiIncludeWithExcludeCategorySuite.class);
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
    }

    public static class ClassAsCategory {
    }

    public static class OneMoreTest {
        @Category(ClassAsCategory.class)
        @Test
        public void a() {
        }
    }

    @RunWith(Categories.class)
    @IncludeCategory(ClassAsCategory.class)
    @SuiteClasses({OneMoreTest.class})
    public static class RunClassAsCategory {
    }

    @Test
    public void classesCanBeCategories() {
        assertThat(testResult(RunClassAsCategory.class), isSuccessful());
    }

    @Category(SlowTests.class)
    public abstract static class Ancestor{}

    public static class Inherited extends Ancestor {
        @Test
        public void a(){
        }
    }

    @RunWith(Categories.class)
    @IncludeCategory(SlowTests.class)
    @SuiteClasses(Inherited.class)
    public interface InheritanceSuite {}

    @Test
    public void testInheritance() {
        Result result = JUnitCore.runClasses(InheritanceSuite.class);
        assertEquals(1, result.getRunCount());
        assertTrue(result.wasSuccessful());
    }

    @RunWith(Categories.class)
    @IncludeCategory(Runnable.class)
    @ExcludeCategory(Runnable.class)
    @SuiteClasses({})
    public static class EmptyCategoriesSuite {
    }

    @Test public void emptyCategoriesSuite() {
        assertThat(testResult(EmptyCategoriesSuite.class), failureCountIs(1));
    }

    @Category(Runnable.class)
    public static class NoTest {
    }

    @Category(Runnable.class)
    public static class IgnoredTest {

        @Ignore
        @Test
        public void test() {
            fail();
        }
    }

    @RunWith(Categories.class)
    @IncludeCategory(Runnable.class)
    @SuiteClasses({NoTest.class, IgnoredTest.class})
    public static class IgnoredTestCategoriesSuite {
    }

    @Test
    public void ignoredTest() {// behaves same as Suite
        Result result= JUnitCore.runClasses(IgnoredTestCategoriesSuite.class);
        assertFalse(result.wasSuccessful());
        assertThat(result.getRunCount(), is(1));
        assertThat(result.getFailureCount(), is(1));
        assertThat(result.getIgnoreCount(), is(1));
    }

    @Category(Runnable.class)
    public static class ExcludedTest1 {

        @Test
        public void test() {
            fail();
        }
    }

    @Category(Runnable.class)
    public static class ExcludedTest2 {

        @Test
        @Category(Runnable.class)
        public void test() {
            fail();
        }
    }

    public static class IncludedTest {

        @Test
        @Category(Object.class)
        public void test() {
        }
    }

    @RunWith(Categories.class)
    @IncludeCategory({Runnable.class, Object.class})
    @ExcludeCategory(Runnable.class)
    @SuiteClasses({ExcludedTest1.class, ExcludedTest2.class, IncludedTest.class})
    public static class IncludedExcludedSameSuite {
    }

    @Test
    public void oneRunnableOthersAvoided() {
        Result result= JUnitCore.runClasses(IncludedExcludedSameSuite.class);
        assertEquals(1, result.getRunCount());
        assertTrue(result.wasSuccessful());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCountWithMultipleExcludeFilter() throws Throwable {
        Set<Class<?>> exclusions= new HashSet<Class<?>>(2);
        Collections.addAll(exclusions, SlowTests.class, FastTests.class);
        CategoryFilter exclude = CategoryFilter.categoryFilter(true, null, true, exclusions);
        Request baseRequest= Request.aClass(OneOfEach.class);
        Result result= new JUnitCore().run(baseRequest.filterWith(exclude));
        assertTrue(result.wasSuccessful());
        assertEquals(1, result.getRunCount());
    }

    @Test
    public void testCountWithMultipleIncludeFilter() throws Throwable {
        CategoryFilter exclude = CategoryFilter.include(true, SlowTests.class, FastTests.class);
        Request baseRequest= Request.aClass(OneOfEach.class);
        Result result= new JUnitCore().run(baseRequest.filterWith(exclude));
        assertTrue(result.wasSuccessful());
        assertEquals(2, result.getRunCount());
    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory(String.class)
    @Suite.SuiteClasses(NoIncludeCategoryAnnotationTest.class)
    public static class NoIncludeCategoryAnnotationSuite {
    }

    @Category(CharSequence.class)
    public static class NoIncludeCategoryAnnotationTest {

        @Test
        public void test2() {
        }

        @Test
        @Category(String.class) public void test1() {
        }
    }

    @Test
    public void noIncludeCategoryAnnotation() {
        Result testResult= JUnitCore.runClasses(NoIncludeCategoryAnnotationSuite.class);
        assertTrue(testResult.wasSuccessful());
        assertEquals(1, testResult.getRunCount());
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CharSequence.class)
    @Categories.ExcludeCategory(String.class)
    @Suite.SuiteClasses(NoIncludeCategoryAnnotationTest.class)
    public static class SameAsNoIncludeCategoryAnnotationSuite {
    }

    @Test
    public void sameAsNoIncludeCategoryAnnotation() {
        Result testResult= JUnitCore.runClasses(SameAsNoIncludeCategoryAnnotationSuite.class);
        assertTrue(testResult.wasSuccessful());
        assertEquals(1, testResult.getRunCount());
    }
}
