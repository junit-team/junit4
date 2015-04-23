package org.junit.runner;

import org.junit.runner.manipulation.Filter;

/**
 * Extend this class to create a factory that creates {@link Filter}.
 */
public interface FilterFactory {
    /**
     * Creates a {@link Filter} given a {@link FilterFactoryParams} argument.
     *
     * @param params Parameters needed to create the {@link Filter}
     */
    Filter createFilter(FilterFactoryParams params) throws FilterNotCreatedException;

    /**
     * Exception thrown if the {@link Filter} cannot be created.
     */
    @SuppressWarnings("serial")
    class FilterNotCreatedException extends Exception {
        public FilterNotCreatedException(Exception exception) {
            super(exception.getMessage(), exception);
        }
    }
}
