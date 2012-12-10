package org.junit.tests.experimental.categories;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static junit.framework.Assert.assertEquals;

/**
 * Author: Henning Gross
 * Date: 04.12.12
 */
public class MultipleClassesInIncludeCategoryAndExludeCategoryTest {

    // category markers
    public interface IntegrationTests {
    }

    public interface SlowTests {
    }

    public static class A {
        @Category(IntegrationTests.class)
        @Test
        public void a() {
        }

        @Category(SlowTests.class)
        @Test
        public void b() {
        }

        @Test
        public void c() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory({SlowTests.class, IntegrationTests.class})
    @Suite.SuiteClasses({A.class})
    public static class IncludeCategorySuite {
    }

    @Test
    public void testMultipleClassesInIncludeCategory() {
        // a and b will be ran
        Result result = JUnitCore.runClasses(IncludeCategorySuite.class);
        assertEquals(2, result.getRunCount());
    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory({SlowTests.class, IntegrationTests.class})
    @Suite.SuiteClasses({A.class})
    public static class ExcludeCategorySuite {
    }

    @Test
    public void testMultipleClassesInExcludeCategory() {
        // only c will be ran (not annotated, due to no @IncludeCategory specified)
        Result result = JUnitCore.runClasses(ExcludeCategorySuite.class);
        assertEquals(1, result.getRunCount());
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(SlowTests.class)
    @Categories.ExcludeCategory({SlowTests.class})
    @Suite.SuiteClasses({A.class})
    public static class OverrideIncludeCategorySuite {
    }

    @Test
    public void testOverrideCategory() {
        // only a will be ran (IntegrationTest)
        Result result = JUnitCore.runClasses(ExcludeCategorySuite.class);
        assertEquals(1, result.getRunCount());
    }
}
