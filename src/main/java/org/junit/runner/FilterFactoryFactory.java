package org.junit.runner;

import org.junit.runner.manipulation.Filter;

/**
 * Extend this class to create a factory that creates a factory that creates a {@link Filter}.
 */
class FilterFactoryFactory {
    /**
     * Creates a {@link Filter}.
     *
     * A filter specification is of the form "package.of.FilterFactory=args-to-filter-factory" or
     * "package.of.FilterFactory".
     *
     * @param filterSpec The filter specification
     * @throws FilterFactoryNotCreatedException
     * @throws FilterFactory.FilterNotCreatedException
     */
    public Filter createFilterFromFilterSpec(String filterSpec)
            throws FilterFactoryNotCreatedException, FilterFactory.FilterNotCreatedException {
        String filterFactoryFqcn;
        FilterFactoryParams args;

        if (filterSpec.contains("=")) {
            String[] tuple = filterSpec.split("=", 2);

            filterFactoryFqcn = tuple[0];
            args = new FilterFactoryParams.OneArg(tuple[1]);
        } else {
            filterFactoryFqcn = filterSpec;
            args = new FilterFactoryParams.ZeroArg();
        }

        return createFilter(filterFactoryFqcn, args);
    }

    /**
     * Creates a {@link Filter}.
     *
     * @param filterFactoryClass The class of the {@link FilterFactory}
     * @param args The arguments to the {@link FilterFactory}
     * @throws FilterFactory.FilterNotCreatedException
     * @throws FilterFactoryNotCreatedException
     */
    public Filter createFilter(Class<? extends FilterFactory> filterFactoryClass, FilterFactoryParams args)
            throws FilterFactory.FilterNotCreatedException, FilterFactoryNotCreatedException {
        return createFilter(filterFactoryClass.getName(), args);
    }

    /**
     * Creates a {@link Filter}.
     *
     * @param filterFactoryFqcn The fully qualified class name of the {@link FilterFactory}
     * @param args The arguments to the {@link FilterFactory}
     * @throws FilterFactory.FilterNotCreatedException
     * @throws FilterFactoryNotCreatedException
     */
    public Filter createFilter(String filterFactoryFqcn, FilterFactoryParams args)
            throws FilterFactory.FilterNotCreatedException, FilterFactoryNotCreatedException {
        return args.apply(filterFactoryFqcn);
    }

    /**
     * Exception thrown if the {@link FilterFactory} cannot be created.
     */
    public static class FilterFactoryNotCreatedException extends ClassNotFoundException {
        public FilterFactoryNotCreatedException(String message) {
            super(message);
        }
    }
}
