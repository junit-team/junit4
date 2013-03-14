package org.junit.runner;

import org.junit.Test;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.runner.manipulation.Filter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.runner.FilterFactory.NoFilterFactoryParams;

public class FilterFactoryFactoryTest {
    @Test
    public void shouldCreateFilterWithArguments() throws Exception {
        FilterFactoryFactory filterFactoryFactory = new FilterFactoryFactory();
        Filter filter = filterFactoryFactory.createFilterFromFilterSpec(
                ExcludeCategories.class.getName() + "=" + DummyCategory.class.getName());

        assertThat(filter.describe(), startsWith("excludes "));
    }

    @Test
    public void shouldCreateFilterWithNoArguments() throws Exception {
        FilterFactoryFactory filterFactoryFactory = new FilterFactoryFactory();
        Filter filter = filterFactoryFactory.createFilterFromFilterSpec(FilterFactoryStub.class.getName());

        assertThat(filter, instanceOf(DummyFilter.class));
    }

    @Test
    public void shouldCreateFilter() throws Exception {
        FilterFactoryFactory filterFactoryFactory = new FilterFactoryFactory();
        Filter filter = filterFactoryFactory.createFilter(FilterFactoryStub.class, new NoFilterFactoryParams());

        assertThat(filter, instanceOf(DummyFilter.class));
    }

    public static class FilterFactoryStub extends FilterFactory {
        @Override
        public Filter createFilter(FilterFactoryParams unused) {
            return new DummyFilter();
        }
    }

    private static class DummyFilter extends Filter {
        @Override
        public boolean shouldRun(Description description) {
            return false;
        }

        @Override
        public String describe() {
            return null;
        }
    }

    public static class DummyCategory {
    }
}
