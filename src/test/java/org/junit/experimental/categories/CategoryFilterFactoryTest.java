package org.junit.experimental.categories;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.FilterFactory;
import org.junit.runner.manipulation.Filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CategoryFilterFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldCreateFilter() throws Exception {
        CategoryFilterFactory categoryFilterFactory = new CategoryFilterFactoryStub();
        Filter filter = categoryFilterFactory.createFilter(CategoryFilterFactoryStub.class.getName());

        assertThat(filter, is((Filter) null));
    }

    @Test
    public void shouldThrowException() throws Exception {
        expectedException.expect(FilterFactory.FilterNotCreatedException.class);

        CategoryFilterFactory categoryFilterFactory = new CategoryFilterFactoryStub();
        Filter filter = categoryFilterFactory.createFilter("NonExistentFilter");

        assertThat(filter, is((Filter) null));
    }

    public static class DummyCategory {
    }

    private static class CategoryFilterFactoryStub extends CategoryFilterFactory {
        @Override
        protected Filter createFilter(Class<?>[] categories) {
            return null;
        }
    }
}
