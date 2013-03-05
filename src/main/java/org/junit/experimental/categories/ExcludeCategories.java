package org.junit.experimental.categories;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.runner.manipulation.Filter;

import static org.junit.experimental.categories.Categories.CategoryFilter;

/**
 * {@link org.junit.runner.FilterFactory} to exclude categories.
 *
 * The {@link Filter} that is created will filter out tests that are categorized with any of the given categories.
 *
 * Usage from command line:
 * <code>
 *     --filter=org.junit.experimental.categories.ExcludeCategories=package.of.FirstCategory,package.of.SecondCategory
 * </code>
 *
 * Usage from API:
 * <code>
 *     new ExcludeCategories().createFilter(new Class[]{
 *         FirstCategory.class,
 *         SecondCategory.class
 *     });
 * </code>
 */
public final class ExcludeCategories extends CategoryFilterFactory {
    @Override
    public Filter createFilter(Class<?>[] categories) {
        return new ExcludesAny(categories);
    }

    public static class ExcludesAny extends CategoryFilter {
        public ExcludesAny(Class<?>[] categories) {
            this(new HashSet<Class<?>>(Arrays.asList(categories)));
        }

        public ExcludesAny(Set<Class<?>> categories) {
            super(true, null, true, categories);
        }
    }
}
