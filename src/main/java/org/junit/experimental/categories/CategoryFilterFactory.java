package org.junit.experimental.categories;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.ClassUtil;
import org.junit.runner.FilterFactory;
import org.junit.runner.manipulation.Filter;

abstract class CategoryFilterFactory extends FilterFactory {
    @Override
    public Filter createFilter(String categories) throws FilterNotCreatedException {
        try {
            return createFilter(parseCategories(categories));
        } catch (Exception e) {
            throw new FilterNotCreatedException("Could not create filter.", e);
        }
    }

    protected abstract Filter createFilter(Class<?>[] categories);

    Class<?>[] parseCategories(String categories) throws ClassNotFoundException {
        List<Class<?>> categoryClasses = new ArrayList<Class<?>>();

        for (String category : categories.split(",")) {
            Class<?> categoryClass = ClassUtil.getClass(category);

            categoryClasses.add(categoryClass);
        }

        return categoryClasses.toArray(new Class[]{});
    }
}
