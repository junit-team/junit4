package org.junit.filters;

import java.util.regex.Pattern;

import org.junit.experimental.categories.Category;

import static org.junit.filters.ClassUtil.convertFqnToClassPath;

public class CategoriesExcludeFilter extends CategoriesFilter {
    private final String categoriesExcludeAntGlob;
    private final Pattern categoriesExcludeRegex;

    public CategoriesExcludeFilter(final String categoriesExcludeAntGlob) {
        this.categoriesExcludeAntGlob = categoriesExcludeAntGlob;
        this.categoriesExcludeRegex = Pattern.compile(
                AntGlobConverter.convertCommaSeparatedList(categoriesExcludeAntGlob));
    }

    @Override
    public boolean shouldRun(final Category category) {
        return !inSomeExcludeCategories(category);
    }

    @Override
    public String describe() {
        return "Excluded categories = " + categoriesExcludeAntGlob;
    }

    private boolean inSomeExcludeCategories(final Category categoryAnnotation) {
        if (DEFAULT.equals(categoriesExcludeAntGlob) || categoryAnnotation == null) {
            return false;
        }

        for (Class<?> category : categoryAnnotation.value()) {
            if (categoriesExcludeRegex.matcher(convertFqnToClassPath(category.getName())).matches()) {
                return true;
            }
        }

        return false;
    }
}
