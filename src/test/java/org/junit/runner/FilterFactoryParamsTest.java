package org.junit.runner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.experimental.categories.IncludeCategories;
import org.junit.rules.ExpectedException;
import org.junit.runner.manipulation.Filter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.runner.FilterFactory.FilterNotCreatedException;
import static org.junit.runner.FilterFactoryFactory.FilterFactoryNotCreatedException;
import static org.junit.runner.FilterFactoryParams.OneArg;

public class FilterFactoryParamsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowFilterNotCreatedException() throws Exception {
        expectedException.expect(FilterNotCreatedException.class);

        FilterFactoryParams filterFactoryParams = new FilterFactoryParamsStubThatThrowsFilterNotCreatedException();

        filterFactoryParams.apply(IncludeCategories.class.getName());
    }

    @Test
    public void shouldThrowFilterFactoryNotCreatedException() throws Exception {
        expectedException.expect(FilterFactoryNotCreatedException.class);

        FilterFactoryParams filterFactoryParams = new FilterFactoryParamsStubThatThrowsException();

        filterFactoryParams.apply(IncludeCategories.class.getName());
    }

    @Test
    public void shouldCreateFilter() throws Exception {
        FilterFactoryParams filterFactoryParams = new OneArg(DummyCategory.class.getName());

        Filter filter = filterFactoryParams.apply(ExcludeCategories.class.getName());

        assertThat(filter, instanceOf(ExcludeCategories.ExcludesAny.class));
    }

    public static class DummyCategory {
    }

    private class FilterFactoryParamsStubThatThrowsFilterNotCreatedException extends FilterFactoryParams {
        @Override
        public Filter apply(FilterFactory filterFactory) throws Exception {
            throw new FilterNotCreatedException("stub", new Exception());
        }
    }

    private class FilterFactoryParamsStubThatThrowsException extends FilterFactoryParams {
        @Override
        public Filter apply(FilterFactory filterFactory) throws Exception {
            throw new Exception("stub");
        }
    }
}
