package org.junit.runner;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.filters.IgnoreFilter;
import org.junit.rules.ExpectedException;
import org.junit.runner.manipulation.Filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterFactoryTest {
    @Rule
    public final ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void shouldProcessFiltersWithNoArguments() throws Exception {
        final FilterFactory filterFactory = new FilterFactory();
        final Filter filter =
                filterFactory.createFilter(IgnoreFilter.class.getName());
        final Description description = Description.createSuiteDescription(IgnoredTestClass.class);

        assertFalse(filter.shouldRun(description));
    }

    @Test
    public void shouldProcessFiltersWithOneArgument() throws Exception {
        final FilterFactory filterFactory = new FilterFactory();
        final Filter filter = filterFactory.createFilter(
                Categories.CategoryFilter.IncludesAny.class.getName() + "=" + DummyCategory0.class.getName());

        assertFalse(filter.shouldRun(Description.createSuiteDescription(DummyTestClass.class)));
        assertTrue(filter.shouldRun(Description.createSuiteDescription(DummyTestClass0.class)));
    }

    @Test
    public void shouldWrapException() throws Exception {
        final FilterFactory filterFactory = new FilterFactory();

        exceptionRule.expect(FilterFactory.FilterNotFoundException.class);

        filterFactory.createFilter("package.of.NonExistentFilter.class");
    }

    @Ignore
    private static class IgnoredTestClass {}

    private static class DummyTestClass {}

    @Category(DummyCategory0.class)
    private static class DummyTestClass0 {}

    private static interface DummyCategory0 {}
}
