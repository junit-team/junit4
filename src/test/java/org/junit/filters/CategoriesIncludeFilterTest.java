package org.junit.filters;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.Description;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author nyap@netflix.com (Noel Yap)
 */
public class CategoriesIncludeFilterTest {
    @Test
    public void uncategorizedTestShouldNotRun() {
        final CategoriesIncludeFilter categoriesIncludeFilter = new CategoriesIncludeFilter("dummy-value");

        final Description description = Description.createSuiteDescription(UncategorizedTestClass.class);
        final Category category = description.getAnnotation(Category.class);

        assertFalse(categoriesIncludeFilter.shouldRun(category));
    }

    @Test
    public void testShouldRunIfNoCategoriesAreIncluded() {
        final CategoriesIncludeFilter categoriesIncludeFilter = new CategoriesIncludeFilter("");

        final Description description = Description.createSuiteDescription(CategorizedTestClass.class);
        final Category category = description.getAnnotation(Category.class);

        assertTrue(categoriesIncludeFilter.shouldRun(category));
    }

    @Test
    public void testShouldNotRunIfItDoesNotHaveAllIncludedCategories() {
        final CategoriesIncludeFilter categoriesIncludeFilter =
                new CategoriesIncludeFilter("**/*$DummyCategory1.class,**/*$DummyCategory2.class");

        final Description description = Description.createSuiteDescription(CategorizedTestClass.class);
        final Category category = description.getAnnotation(Category.class);

        assertFalse(categoriesIncludeFilter.shouldRun(category));
    }

    @Test
    public void testShouldRunIfOnlySomeOfItsCategoriesAreIncluded() {
        final CategoriesIncludeFilter categoriesIncludeFilter =
                new CategoriesIncludeFilter("**/*$DummyCategory1.class");

        final Description description = Description.createSuiteDescription(CategorizedTestClass.class);
        final Category category = description.getAnnotation(Category.class);

        assertTrue(categoriesIncludeFilter.shouldRun(category));
    }

    private static class UncategorizedTestClass {}

    @Category({DummyCategory0.class, DummyCategory1.class})
    private static class CategorizedTestClass {}

    private static interface DummyCategory0 {}
    private static interface DummyCategory1 {}
}
