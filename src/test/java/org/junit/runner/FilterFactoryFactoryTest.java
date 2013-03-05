package org.junit.runner;

import org.junit.Test;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.runner.manipulation.Filter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FilterFactoryFactoryTest {
    @Test
    public void shouldCreateFilterWithArguments() throws Exception {
        FilterFactoryFactory filterFactoryFactory = new FilterFactoryFactory();
        Filter filter = filterFactoryFactory.createFilterFromFilterSpec(
                ExcludeCategories.class.getName() + "=" + DummyCategory.class.getName());

        assertThat(filter, instanceOf(ExcludeCategories.ExcludesAny.class));
    }

    @Test
    public void shouldCreateFilterWithNoArguments() throws Exception {
        FilterFactoryFactory filterFactoryFactory = new FilterFactoryFactory();
        Filter filter = filterFactoryFactory.createFilterFromFilterSpec(FilterFactoryStub.class.getName());

        assertThat(filter, is((Filter) null));
    }

    @Test
    public void shouldCreateFilter() throws Exception {
        FilterFactoryFactory filterFactoryFactory = new FilterFactoryFactory();
        Filter filter = filterFactoryFactory.createFilter(FilterFactoryStub.class, new FilterFactoryParams.ZeroArg());

        assertThat(filter, is((Filter) null));
    }

    public static class FilterFactoryStub extends FilterFactory {
        @Override
        public Filter createFilter() {
            return null;
        }
    }

    public static class DummyCategory {
    }
}
