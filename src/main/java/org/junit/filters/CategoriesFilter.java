package org.junit.filters;

import org.junit.experimental.categories.Category;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

abstract class CategoriesFilter extends Filter {
    public static final String DEFAULT = "";

    @Override
    public boolean shouldRun(final Description description) {
        if (description.getMethodName() == null) {
            if (description.getChildren().isEmpty()) {
                final Category classCategory = description.getAnnotation(Category.class);

                return shouldRun(classCategory);
            }

            return true;
        }

        final Category methodCategory = description.getAnnotation(Category.class);

        return shouldRun(methodCategory);
    }

    public abstract boolean shouldRun(final Category category);
}
