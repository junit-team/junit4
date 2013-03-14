package org.junit.runner;

import org.junit.runner.manipulation.Filter;

/**
 * Extend this class to create a factory that creates {@link Filter}.
 */
public abstract class FilterFactory {
    public FilterFactoryParams parseArgs(String args) throws FilterNotCreatedException {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a {@link Filter} using a ${FilterFactoryParams} argument.
     *
     * @param params Parameters needed to create the {@link Filter}
     * @throws FilterNotCreatedException
     */
    abstract public Filter createFilter(FilterFactoryParams params) throws FilterNotCreatedException;

    public static class NoFilterFactoryParams implements FilterFactoryParams {
    }

    /**
     * Exception thrown if the {@link Filter} cannot be created.
     */
    public static class FilterNotCreatedException extends FilterFactoryFactory.FilterFactoryNotCreatedException {
        public FilterNotCreatedException(String message) {
            super(message);
        }
    }
}
