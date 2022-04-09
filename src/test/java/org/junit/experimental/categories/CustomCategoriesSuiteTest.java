package org.junit.experimental.categories;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class CustomCategoriesSuiteTest {

    public static class CustomCategories extends Categories {
        public CustomCategories(Class<?> klass, RunnerBuilder builder)
                throws InitializationError {
            super(builder, klass, suiteClasses());
        }

        private static Class<?>[] suiteClasses() {
            // Here a custom algorithm could be invoked to determine which
            // classes should be run by the test suite.
            // e.g. Reflections lib. (org.reflections) could be used to
            // determine subclasses of a abstract test class
            // which could be provided to the test suite by a custom annotation.
            return new Class<?>[] { TestWithCategory.class };
        }
    }

    public static class TestCategory {
    }

    @Category(TestCategory.class)
    public static class TestWithCategory {
        @Parameters
        public static Iterable<String> getParameters() {
            return Arrays.asList("first", "second");
        }

        @Parameterized.Parameter
        public String value;

        @Test
        public void testSomething() {
            Assert.assertTrue(true);
        }
    }

    @RunWith(CustomCategories.class)
    @IncludeCategory(TestCategory.class)
    public static class SuiteWithTestWithCategory {
    }

    @Test
    public void runsTestsWithCategory() {
        Result result = new JUnitCore().run(SuiteWithTestWithCategory.class);
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
    }
}
