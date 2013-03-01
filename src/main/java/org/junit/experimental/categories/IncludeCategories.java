package org.junit.experimental.categories;

import org.junit.runner.manipulation.Filter;

import static org.junit.experimental.categories.Categories.CategoryFilter;

public final class IncludeCategories extends CategoryFilter.CategoriesFilterFactory {
    @Override
    public Filter createFilter(Class<?>[] categories) throws FilterNotCreatedException {
        return new CategoryFilter.IncludesAny(categories);
    }
}
