package org.junit.runner;

import org.junit.runner.manipulation.Filter;

public abstract class FilterFactoryParams {
    public Filter apply(String filterFactoryFqcn)
            throws FilterFactory.FilterNotCreatedException, FilterFactoryFactory.FilterFactoryNotFoundException {
        try {
            return apply(Class.forName(filterFactoryFqcn, true, Thread.currentThread().getContextClassLoader())
                    .asSubclass(FilterFactory.class)
                    .getConstructor()
                    .newInstance());
        } catch (final Exception e) {
            throw new FilterFactoryFactory.FilterFactoryNotFoundException(e.getMessage());
        }
    }

    abstract public Filter apply(FilterFactory filterFactory) throws Exception;

    public static class ZeroArg extends FilterFactoryParams {
        @Override
        public Filter apply(FilterFactory filterFactory) throws FilterFactory.FilterNotCreatedException {
            return filterFactory.createFilter();
        }
    }

    public static class OneArg extends FilterFactoryParams {
        private final String args;

        public OneArg(String args) {
            this.args = args;
        }

        @Override
        public Filter apply(FilterFactory filterFactory) throws FilterFactory.FilterNotCreatedException {
            return filterFactory.createFilter(args);
        }
    }
}
