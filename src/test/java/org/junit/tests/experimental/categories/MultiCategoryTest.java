package org.junit.tests.experimental.categories;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.Selection;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import static org.junit.matchers.JUnitMatchers.hasItems;

/**
 * @author tibor17
 * @version 4.11
 * @since 4.11, 17.3.2012, 14:45
 */
public final class MultiCategoryTest {
    public interface A {}
    public interface B {}
    public interface C {}

    static enum TestCategory { A, B, C }

    @Test
    public void runSuite() { shouldRun(MultiCategorySuite.class, TestCategory.A, TestCategory.B); }

    private static void shouldRun(final Class<?> junitTestType, final TestCategory... expectedPlatforms) {

        // Targeting Test:
        final PrintableResult testResult = testResult(junitTestType);

        final Set<TestCategory> passedTestCases = CategoriesTest.passedTestCases;

        assertThat("unexpected size", passedTestCases.size(), is(equalTo(expectedPlatforms.length)));
        assertThat(passedTestCases, hasItems(expectedPlatforms));

        passedTestCases.clear();

        assertThat("wrong test modifications, and broken collection of expectations", testResult, isSuccessful());
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(value = {A.class, B.class}, assignableTo = Selection.ANY)
    @Categories.ExcludeCategory(C.class)
    @Suite.SuiteClasses({CategoriesTest.class})
    public static final class MultiCategorySuite {}

    public static final class CategoriesTest {
        static final Set<TestCategory> passedTestCases = new HashSet<TestCategory>();

        @Test
        @Category(A.class)
        public void a() { passedTestCases.add(TestCategory.A); }

        @Test
        @Category(B.class)
        public void b() { passedTestCases.add(TestCategory.B); }

        @Test
        @Category(C.class)
        public void c() {
            fail();
        }

        @Test
        public void anything() {
            fail();
        }
    }
}
