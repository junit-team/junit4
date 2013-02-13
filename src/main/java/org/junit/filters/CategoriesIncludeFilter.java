package org.junit.filters;

import java.util.regex.Pattern;

import org.junit.experimental.categories.Category;

import static org.junit.filters.ClassUtil.convertFqnToClassPath;

public class CategoriesIncludeFilter extends CategoriesFilter {
    private final String categoriesIncludeAntGlob;
    private final String categoriesIncludeRegex;

    public CategoriesIncludeFilter(final String categoriesIncludeAntGlob) {
        this.categoriesIncludeAntGlob = categoriesIncludeAntGlob;
        this.categoriesIncludeRegex = AntGlobConverter.convert(categoriesIncludeAntGlob);
    }

    @Override
    public boolean shouldRun(final Category category) {
        return inAllIncludeCategories(category);
    }

    @Override
    public String describe() {
        return "Included categories = " + categoriesIncludeAntGlob;
    }

    private boolean inAllIncludeCategories(final Category categoryAnnotation) {
        if (DEFAULT.equals(categoriesIncludeAntGlob)) {
            return true;
        } else if (categoryAnnotation == null) {
            return false;
        }

        for (String categoryRegex : categoriesIncludeRegex.split(",")) {
            if (!inSomeIncludeCategories(categoryAnnotation, categoryRegex)) {
                return false;
            }
        }

        return true;
    }

    private boolean inSomeIncludeCategories(final Category categoryAnnotation, final String categoryRegex) {
        final Pattern categoriesPattern = Pattern.compile(categoryRegex);

        for (Class<?> category : categoryAnnotation.value()) {
            if (categoriesPattern.matcher(convertFqnToClassPath(category.getName())).matches()) {
                return true;
            }
        }

        return false;
    }
}
