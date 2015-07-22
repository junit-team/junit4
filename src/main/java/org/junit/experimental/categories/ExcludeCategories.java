package org.junit.experimental.categories;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.runner.manipulation.Filter;

/**
 * {@link org.junit.runner.FilterFactory} to exclude categories.
 *
 * The {@link Filter} that is created will filter out tests that are categorized with any of the
 * given categories.
 *
 * Usage from command line:
 * <code>
 *     --filter=org.junit.experimental.categories.ExcludeCategories=pkg.of.Cat1,pkg.of.Cat2
 * </code>
 *
 * Usage from API:
 * <code>
 *     new ExcludeCategories().createFilter(Cat1.class, Cat2.class);
 * </code>
 */
public final class ExcludeCategories extends CategoryFilterFactory {
    /**
     * Creates a {@link Filter} which is only passed by tests that are
     * not categorized with any of the specified categories.
     *
     * @param categories Category classes.
     */
    @Override
    protected Filter createFilter(List<Class<?>> categories) {
        return new ExcludesAny(categories);
    }

    private static class ExcludesAny extends CategoryFilter {
        public ExcludesAny(List<Class<?>> categories) {
            this(new HashSet<Class<?>>(categories));
        }

        public ExcludesAny(Set<Class<?>> categories) {
            super(true, null, true, categories);
        }

        @Override
        public String describe() {
            return "excludes " + super.describe();
        }
    }
}
