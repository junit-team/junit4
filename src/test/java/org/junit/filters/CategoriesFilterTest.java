package org.junit.filters;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.Description;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CategoriesFilterTest {
    @Test
    public void testSuiteShouldRun() {
        final NotDummyCategoryFilter notDummyCategoryFilter = new NotDummyCategoryFilter();

        final Description description = Description.createSuiteDescription(DummySuite.class);
        description.addChild(Description.createSuiteDescription(CategoriesFilterTest.class));

        assertTrue(notDummyCategoryFilter.shouldRun(description));
    }

    @Test
    public void categorizedTestClassShouldRun() {
        final NotDummyCategoryFilter notDummyCategoryFilter = new NotDummyCategoryFilter();

        final Description description = Description.createSuiteDescription(CategorizedClassTestClass.class);

        assertFalse(notDummyCategoryFilter.shouldRun(description));
    }

    @Test
    public void categorizedTestMethodShouldRun() throws Exception {
        final NotDummyCategoryFilter notDummyCategoryFilter = new NotDummyCategoryFilter();

        final Description description = Description.createTestDescription(
                CategorizedMethodTestClass.class,
                "test",
                CategorizedMethodTestClass.class.getDeclaredMethod("dummyTest").getAnnotations());

        assertFalse(notDummyCategoryFilter.shouldRun(description));
    }

    private class NotDummyCategoryFilter extends CategoriesFilter {
        @Override
        public boolean shouldRun(final Category categoryAnnotation) {
            return !categoryAnnotation.value()[0].getName().contains("DummyCategory");
        }

        @Override
        public String describe() {
            return null;
        }
    }

    private class DummySuite {}

    @Category(DummyCategory.class)
    private static class CategorizedClassTestClass {
        @Test
        public void dummyTest() {
        }
    }

    private static class CategorizedMethodTestClass {
        @Category(DummyCategory.class)
        @Test
        public void dummyTest() {
        }
    }

    private static interface DummyCategory {}
}
