package org.junit.experimental.categories;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.ClassUtil;
import org.junit.runner.FilterFactory;
import org.junit.runner.FilterFactoryParams;
import org.junit.runner.manipulation.Filter;

/**
 * Implementation of FilterFactory for Category filtering.
 */
public abstract class CategoryFilterFactory extends FilterFactory {
    @Override
    public FilterFactoryParams parseArgs(String args) throws FilterNotCreatedException {
        try {
            return new CategoryFilterFactoryParams(parseCategories(args));
        } catch (ClassNotFoundException e) {
            throw new FilterNotCreatedException(e.getMessage());
        }
    }

    @Override
    public Filter createFilter(FilterFactoryParams params) {
        return createFilter(((CategoryFilterFactoryParams) params).getCategories());
    }

    protected abstract Filter createFilter(Class<?>[] categories);

    private Class<?>[] parseCategories(String categories) throws ClassNotFoundException {
        List<Class<?>> categoryClasses = new ArrayList<Class<?>>();

        for (String category : categories.split(",")) {
            Class<?> categoryClass = ClassUtil.getClass(category);

            categoryClasses.add(categoryClass);
        }

        return categoryClasses.toArray(new Class[]{});
    }

    public class CategoryFilterFactoryParams implements FilterFactoryParams {
        private final Class<?>[] categories;

        public CategoryFilterFactoryParams(Class<?>[] categories) {
            this.categories = categories;
        }

        public Class<?>[] getCategories() {
            return categories;
        }
    }
}
