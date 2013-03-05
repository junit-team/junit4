package org.junit.runner;

import org.junit.internal.ClassUtil;
import org.junit.runner.manipulation.Filter;

import static org.junit.runner.FilterFactoryFactory.FilterFactoryNotCreatedException;

/**
 * Parameters to a {@link FilterFactory}.
 */
public abstract class FilterFactoryParams {
    /**
     * Creates a {@link Filter} by applying the parameters to a {@link FilterFactory}
     *
     * @param filterFactoryFqcn The fully qualified class name of a {@link FilterFactory}
     * @throws FilterFactory.FilterNotCreatedException
     * @throws FilterFactoryNotCreatedException
     */
    public Filter apply(String filterFactoryFqcn)
            throws FilterFactory.FilterNotCreatedException, FilterFactoryNotCreatedException {
        try {
            FilterFactory filterFactory = ClassUtil.getClass(filterFactoryFqcn)
                    .asSubclass(FilterFactory.class)
                    .getConstructor()
                    .newInstance();

            return apply(filterFactory);
        } catch (FilterFactory.FilterNotCreatedException e) {
            throw e;
        } catch (final Exception e) {
            throw new FilterFactoryNotCreatedException(e.getMessage());
        }
    }

    /**
     * Creates a {@link Filter} by applying the parameters to a {@link FilterFactory}
     *
     * @param filterFactory The {@link FilterFactory}
     * @throws Exception A subclass can throw any Exception if it fails to create a Filter.
     */
    abstract public Filter apply(FilterFactory filterFactory) throws Exception;

    /**
     * FilterFactoryParams representing no arguments.
     */
    public static class ZeroArg extends FilterFactoryParams {
        @Override
        public Filter apply(FilterFactory filterFactory) throws FilterFactory.FilterNotCreatedException {
            return filterFactory.createFilter();
        }
    }

    /**
     * FilterFactoryParams representing one argument.
     */
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
