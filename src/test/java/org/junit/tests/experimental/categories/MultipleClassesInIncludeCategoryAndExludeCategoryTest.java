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
        Result result = JUnitCore.runClasses(ExcludeCategorySuite.class);
        assertEquals(1, result.getRunCount());
    }
}
