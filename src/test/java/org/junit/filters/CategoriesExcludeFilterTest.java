package org.junit.filters;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.Description;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CategoriesExcludeFilterTest {
    @Test
    public void uncategorizedTestShouldRun() {
        final CategoriesExcludeFilter categoriesExcludeFilter = new CategoriesExcludeFilter("dummy-value");

        final Description description = Description.createSuiteDescription(UncategorizedTestClass.class);
        final Category category = description.getAnnotation(Category.class);

        assertTrue(categoriesExcludeFilter.shouldRun(category));
    }

    @Test
    public void testShouldRunIfNoCategoriesAreExcluded() {
        final CategoriesExcludeFilter categoriesExcludeFilter = new CategoriesExcludeFilter("");

        final Description description = Description.createSuiteDescription(CategorizedTestClass.class);
        final Category category = description.getAnnotation(Category.class);

        assertTrue(categoriesExcludeFilter.shouldRun(category));
    }

    @Test
    public void testShouldNotRunIfAnyOfItsCategoriesAreExcluded() {
        final CategoriesExcludeFilter categoriesExcludeFilter =
                new CategoriesExcludeFilter("**/*$DummyCategory1.class,**/*$DummyCategory2.class");

        final Description description = Description.createSuiteDescription(CategorizedTestClass.class);
        final Category category = description.getAnnotation(Category.class);

        assertFalse(categoriesExcludeFilter.shouldRun(category));
    }

    @Test
    public void testShouldRunIfNoneOfItsCategoriesAreExcluded() {
        final CategoriesExcludeFilter categoriesExcludeFilter =
                new CategoriesExcludeFilter("**/*$DummyCategory2.class");

        final Description description = Description.createSuiteDescription(CategorizedTestClass.class);
        final Category category = description.getAnnotation(Category.class);

        assertTrue(categoriesExcludeFilter.shouldRun(category));
    }

    private static class UncategorizedTestClass {}

    @Category({DummyCategory0.class,DummyCategory1.class})
    private static class CategorizedTestClass {}

    private static interface DummyCategory0 {}
    private static interface DummyCategory1 {}
}
