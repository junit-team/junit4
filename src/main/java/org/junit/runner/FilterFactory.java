package org.junit.runner;

import org.junit.runner.manipulation.Filter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Extend this class to create a factory that creates {@link Filter}.
 */
public abstract class FilterFactory {
    /**
     * Creates a {@link Filter} using no arguments.
     *
     * @throws FilterNotCreatedException
     */
    public Filter createFilter() throws FilterNotCreatedException {
        throw new NotImplementedException();
    }

    /**
     * Creates a {@link Filter} using one String argument.
     *
     * @param args Arguments needed to create the {@link Filter}
     * @throws FilterNotCreatedException
     */
    public Filter createFilter(String args) throws FilterNotCreatedException {
        throw new NotImplementedException();
    }

    /**
     * Exception thrown if the {@link Filter} cannot be created.
     */
    public static class FilterNotCreatedException extends ClassNotFoundException {
        public FilterNotCreatedException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
