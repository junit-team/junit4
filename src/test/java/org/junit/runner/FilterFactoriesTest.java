package org.junit.runner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

public class FilterFactoriesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestName testName = new TestName();

    private Request createSuiteRequest() {
        return Request.aClass(DummySuite.class);
    }

    @Test
    public void shouldCreateFilterWithArguments() throws Exception {
        Filter filter = FilterFactories.createFilterFromFilterSpec(
                createSuiteRequest(),
                ExcludeCategories.class.getName() + "=" + DummyCategory.class.getName());

        assertThat(filter.describe(), startsWith("excludes "));
    }

    @Test
    public void shouldCreateFilterWithNoArguments() throws Exception {
        Filter filter = FilterFactories.createFilterFromFilterSpec(
                createSuiteRequest(), FilterFactoryStub.class.getName());

        assertThat(filter, instanceOf(DummyFilter.class));
    }

    @Test
    public void shouldPassOnDescriptionToFilterFactory() throws Exception {
        Request request = createSuiteRequest();
        Description description = request.getRunner().getDescription();
        Filter filter = FilterFactories.createFilterFromFilterSpec(
                request, FilterFactoryStub.class.getName());

        // This assumption tested in shouldCreateFilterWithNoArguments()
        assumeThat(filter, instanceOf(DummyFilter.class));

        DummyFilter dummyFilter = (DummyFilter) filter;
        assertThat(dummyFilter.getTopLevelDescription(), is(description));
    }

    @Test
    public void shouldCreateFilter() throws Exception {
        Filter filter = FilterFactories.createFilter(
                FilterFactoryStub.class,
                new FilterFactoryParams(
                        Description.createSuiteDescription(testName.getMethodName()),
                        ""));

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
        public Filter createFilter(FilterFactoryParams params) {
            return new DummyFilter(params.getTopLevelDescription());
        }
    }

    private static class DummyFilter extends Filter {
        private final Description fTopLevelDescription;

        public DummyFilter(Description topLevelDescription) {
            fTopLevelDescription = topLevelDescription;
        }

        public Description getTopLevelDescription() {
            return fTopLevelDescription;
        }

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

    @RunWith(Suite.class)
    @SuiteClasses(DummyTest.class)
    public static class DummySuite {
    }

    public static class DummyTest {
        @Test
        public void passes() {
        }
    }
}
