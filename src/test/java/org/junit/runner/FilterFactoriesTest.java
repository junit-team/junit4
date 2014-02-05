package org.junit.runner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.manipulation.Filter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.runner.Description.createSuiteDescription;

public class FilterFactoriesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void shouldCreateFilterWithArguments() throws Exception {
        Filter filter = FilterFactories.createFilterFromFilterSpec(
                createSuiteDescription(testName.getMethodName()),
                ExcludeCategories.class.getName() + "=" + DummyCategory.class.getName());

        assertThat(filter.describe(), startsWith("excludes "));
    }

    @Test
    public void shouldCreateFilterWithNoArguments() throws Exception {
        Filter filter = FilterFactories.createFilterFromFilterSpec(
                createSuiteDescription(testName.getMethodName()), FilterFactoryStub.class.getName());

        assertThat(filter, instanceOf(DummyFilter.class));
    }

    @Test
    public void shouldCreateFilter() throws Exception {
        Filter filter = FilterFactories.createFilter(
                FilterFactoryStub.class, new FilterFactoryParams(""));

        assertThat(filter, instanceOf(DummyFilter.class));
    }

    @Test
    public void shouldThrowExceptionIfNotFilterFactory() throws Exception {
        expectedException.expect(FilterFactory.FilterNotCreatedException.class);

        FilterFactories.createFilterFactory(NonFilterFactory.class.getName());
    }

    @Test
    public void shouldThrowExceptionIfNotInstantiable() throws Exception {
        expectedException.expect(FilterFactory.FilterNotCreatedException.class);

        FilterFactories.createFilterFactory(NonInstantiableFilterFactory.class);
    }

    public static class NonFilterFactory {
    }

    public static class NonInstantiableFilterFactory implements FilterFactory {
        private NonInstantiableFilterFactory() {
        }

        public Filter createFilter(FilterFactoryParams params) throws FilterNotCreatedException {
            throw new FilterNotCreatedException(new Exception("not implemented"));
        }
    }

    public static class FilterFactoryStub implements FilterFactory {
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
