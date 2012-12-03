package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.experimental.categories.Category;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

public class CategoryInheritanceTest {

    public interface ATests {
        // category marker
    }

    @Category(value = ATests.class, inherited = true)
    public static class A {
        @Test
        public void a() {
        }
    }

    public static class B extends A {
        @Test
        public void b() {
        }
    }

    @Category(ATests.class)
    public static class C {
        @Test
        public void c() {
        }
    }

    public static class D extends C {
        @Test
        public void d() {
        }
    }


    @RunWith(Categories.class)
    @IncludeCategory(ATests.class)
    @SuiteClasses({ B.class })
    public static class InheritanceSuite {
    }

    @RunWith(Categories.class)
    @IncludeCategory(ATests.class)
    @SuiteClasses({ D.class })
    public static class NoInheritanceSuite {
    }

    @Test
    public void testCountOnInheritance() {
        Result result = JUnitCore.runClasses(InheritanceSuite.class);
        assertEquals(2, result.getRunCount());
    }

    @Test
    public void testCountOnNoInheritance() {
        Result result = JUnitCore.runClasses(NoInheritanceSuite.class);
        assertEquals(1, result.getRunCount());
    }
}
