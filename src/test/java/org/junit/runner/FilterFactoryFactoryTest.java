package org.junit.runner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.rules.ExpectedException;
import org.junit.runner.manipulation.Filter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.runner.FilterFactory.NoFilterFactoryParams;

public class FilterFactoryFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private FilterFactoryFactory filterFactoryFactory = new FilterFactoryFactory();

    @Test
    public void shouldCreateFilterWithArguments() throws Exception {
        Filter filter = filterFactoryFactory.createFilterFromFilterSpec(
                ExcludeCategories.class.getName() + "=" + DummyCategory.class.getName());

        assertThat(filter.describe(), startsWith("excludes "));
    }

    @Test
    public void shouldCreateFilterWithNoArguments() throws Exception {
        Filter filter = filterFactoryFactory.createFilterFromFilterSpec(FilterFactoryStub.class.getName());

        assertThat(filter, instanceOf(DummyFilter.class));
    }

    @Test
    public void shouldCreateFilter() throws Exception {
        Filter filter = filterFactoryFactory.createFilter(FilterFactoryStub.class, new NoFilterFactoryParams());

        assertThat(filter, instanceOf(DummyFilter.class));
    }

    @Test
    public void shouldThrowExceptionIfNotFilterFactory() throws Exception {
        expectedException.expect(FilterFactoryFactory.FilterFactoryNotCreatedException.class);

        filterFactoryFactory.createFilterFactory(NonFilterFactory.class.getName());
    }

    @Test
    public void shouldThrowExceptionIfNotInstantiable() throws Exception {
        expectedException.expect(FilterFactoryFactory.FilterFactoryNotCreatedException.class);

        filterFactoryFactory.createFilterFactory(NonInstantiableFilterFactory.class);
    }

    public static class NonFilterFactory {
    }

    public static class NonInstantiableFilterFactory extends FilterFactory {
        private NonInstantiableFilterFactory() {
        }

        @Override
        public Filter createFilter(FilterFactoryParams params) throws FilterNotCreatedException {
            throw new FilterNotCreatedException("not implemented");
        }
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
