package org.junit.experimental.categories;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.runner.manipulation.Filter;

import static org.junit.experimental.categories.Categories.CategoryFilter;

/**
 * {@link org.junit.runner.FilterFactory} to include categories.
 *
 * The {@link Filter} that is created will filter out tests that are categorized with any of the
 * given categories.
 *
 * Usage from command line:
 * <code>
 *     --filter=org.junit.experimental.categories.IncludeCategories=pkg.of.Cat1,pkg.of.Cat2
 * </code>
 *
 * Usage from API:
 * <code>
 *     new IncludeCategories().createFilter(Cat1.class, Cat2.class);
 * </code>
 */
public final class IncludeCategories extends CategoryFilterFactory {
    @Override
    public Filter createFilter(Class<?>... categories) {
        return new IncludesAny(categories);
    }

    private static class IncludesAny extends CategoryFilter {
        public IncludesAny(Class<?>... categories) {
            this(new HashSet<Class<?>>(Arrays.asList(categories)));
        }

        public IncludesAny(Set<Class<?>> categories) {
            super(true, categories, true, null);
        }

        @Override
        public String describe() {
            return "includes " + super.describe();
        }
    }
}
