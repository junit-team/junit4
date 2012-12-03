package org.junit.tests.experimental.categories;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for option inheritance on {@link Category}
 * <p/>
 * Author: Henning Gross
 * Date: 03.12.12
 */
public class CategoryInheritanceTest {

    // category marker
    public interface Tests {
    }

    @Category(Tests.class)
    public static class A {
        @Test
        public void a() {
            fail();
        }
    }

    public static class B extends A {
        @Test
        public void b() {
            fail();
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(Tests.class)
    @Suite.SuiteClasses({B.class})
    public static class NoInheritanceSuite {
    }

    @Test(expected = NoTestsRemainException.class)
    public void testNoInheritance() throws Throwable {
        // i did not come up with a more clever approach to make sure no test was ran.
        Result result = JUnitCore.runClasses(NoInheritanceSuite.class);
        throw result.getFailures().get(0).getException();
    }


    @Category(value = Tests.class, inherited = true)
    public static class D {
        @Test
        public void d() {
        }
    }

    public static class E extends D {
        @Test
        public void b() {
        }
    }

    public static class F extends E {
        @Test
        public void c() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(Tests.class)
    @Suite.SuiteClasses({F.class})
    public static class InheritanceSuite {
    }

    @Test
    public void testInheritance() {
        Result result = JUnitCore.runClasses(InheritanceSuite.class);
        assertEquals(3, result.getRunCount());
    }
}
