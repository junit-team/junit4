package org.junit.runner;

import org.junit.runner.manipulation.Filter;

class FilterFactoryFactory {
    public Filter createFilterFromFilterSpec(String filterSpec)
            throws FilterFactoryNotFoundException, FilterFactory.FilterNotCreatedException {
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

    public Filter createFilter(Class<? extends FilterFactory> filterFactoryClass, FilterFactoryParams args)
            throws FilterFactory.FilterNotCreatedException, FilterFactoryNotFoundException {
        return createFilter(filterFactoryClass.getName(), args);
    }

    public Filter createFilter(String filterFactoryFqcn, FilterFactoryParams args)
            throws FilterFactory.FilterNotCreatedException, FilterFactoryNotFoundException {
        return args.apply(filterFactoryFqcn);
    }

    public static class FilterFactoryNotFoundException extends ClassNotFoundException {
        public FilterFactoryNotFoundException(String message) {
            super(message);
        }
    }
}
