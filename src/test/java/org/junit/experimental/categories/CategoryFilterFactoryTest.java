package org.junit.experimental.categories;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.Description;
import org.junit.runner.FilterFactoryParams;
import org.junit.runner.manipulation.Filter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class CategoryFilterFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void shouldCreateFilter() throws Exception {
        CategoryFilterFactory categoryFilterFactory = new CategoryFilterFactoryStub();
        FilterFactoryParams params = new FilterFactoryParams(
                Description.createSuiteDescription(testName.getMethodName()),
                CategoryFilterFactoryStub.class.getName());
        Filter filter = categoryFilterFactory.createFilter(params);

        assertThat(filter, instanceOf(DummyFilter.class));
    }

    private static class CategoryFilterFactoryStub extends CategoryFilterFactory {
        @Override
        protected Filter createFilter(Class<?>[] categories) {
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
}
