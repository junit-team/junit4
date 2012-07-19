package org.junit.tests.experimental.categories;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.Selection;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author tibor17
 * @version 4.11
 * @since 4.11, 17.3.2012, 14:45
 */
public final class MultiCategoryTest {
    public interface A {}
    public interface B {}
    public interface C {}

    /**
     * This test is mentioned in {@code Categories} and any changes
     * must be reflected.
     */
    @Test
    public void runSuite() {
        // Targeting Test:
        Result testResult = JUnitCore.runClasses(MultiCategorySuite.class);

        assertThat("unexpected run count", testResult.getRunCount(), is(equalTo(2)));
        assertThat("unexpected failure count", testResult.getFailureCount(), is(equalTo(0)));
        assertThat("unexpected failure count", testResult.getIgnoreCount(), is(equalTo(0)));
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(value = {A.class, B.class}, assignableTo = Selection.ANY)
    @Categories.ExcludeCategory(C.class)
    @Suite.SuiteClasses({CategoriesTest.class})
    public static final class MultiCategorySuite {}

    public static final class CategoriesTest {

        @Test
        @Category(A.class)
        public void a() {}

        @Test
        @Category(B.class)
        public void b() {}

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
