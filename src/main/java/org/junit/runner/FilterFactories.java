package org.junit.runner;

import org.junit.internal.Classes;
import org.junit.runner.manipulation.Filter;

import static org.junit.runner.FilterFactory.FilterNotCreatedException;

/**
 * Utility class whose methods create a {@link FilterFactory}.
 */
public class FilterFactories {
    /**
     * Creates a {@link Filter}.
     *
     * A filter specification is of the form "package.of.FilterFactory=args-to-filter-factory" or
     * "package.of.FilterFactory".
     *
     * @param filterSpec The filter specification
     * @throws FilterFactoryNotCreatedException
     * @throws FilterNotCreatedException
     */
    public static Filter createFilterFromFilterSpec(Description description, String filterSpec)
            throws FilterNotCreatedException {

        if (filterSpec.contains("=")) {
            String[] tuple = filterSpec.split("=", 2);

            return createFilter(tuple[0], new FilterFactoryParams(tuple[1]));
        } else {
            return createFilter(filterSpec, new FilterFactoryParams());
        }
    }

    /**
     * Creates a {@link Filter}.
     *
     * @param filterFactoryFqcn The fully qualified class name of the {@link FilterFactory}
     * @param params The arguments to the {@link FilterFactory}
     * @throws FilterNotCreatedException
     * @throws FilterFactoryNotCreatedException
     */
    public static Filter createFilter(String filterFactoryFqcn, FilterFactoryParams params)
            throws FilterNotCreatedException {
        FilterFactory filterFactory = createFilterFactory(filterFactoryFqcn);

        return filterFactory.createFilter(params);
    }

    /**
     * Creates a {@link Filter}.
     *
     * @param filterFactoryClass The class of the {@link FilterFactory}
     * @param params             The arguments to the {@link FilterFactory}
     * @throws FilterNotCreatedException
     * @throws FilterFactoryNotCreatedException
     *
     */
    public static Filter createFilter(Class<? extends FilterFactory> filterFactoryClass, FilterFactoryParams params)
            throws FilterNotCreatedException {
        FilterFactory filterFactory = createFilterFactory(filterFactoryClass);

        return filterFactory.createFilter(params);
    }

    static FilterFactory createFilterFactory(String filterFactoryFqcn) throws FilterFactoryNotCreatedException {
        Class<? extends FilterFactory> filterFactoryClass;

        try {
            filterFactoryClass = Classes.getClass(filterFactoryFqcn).asSubclass(FilterFactory.class);
        } catch (Exception e) {
            throw new FilterFactoryNotCreatedException(e);
        }

        return createFilterFactory(filterFactoryClass);
    }

    static FilterFactory createFilterFactory(Class<? extends FilterFactory> filterFactoryClass)
            throws FilterFactoryNotCreatedException {
        try {
            return filterFactoryClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new FilterFactoryNotCreatedException(e);
        }
    }

    /**
     * Exception thrown if the {@link FilterFactory} cannot be created.
     */
    public static class FilterFactoryNotCreatedException extends FilterNotCreatedException {
        public FilterFactoryNotCreatedException(Exception exception) {
            super(exception);
        }
    }
}
