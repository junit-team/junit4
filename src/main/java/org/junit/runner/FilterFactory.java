package org.junit.runner;

import org.junit.runner.manipulation.Filter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FilterFactory {
    public Filter createFilter() throws FilterNotFoundException {
        throw new NotImplementedException();
    }

    public Filter createFilter(final String args) throws FilterNotFoundException {
        throw new NotImplementedException();
    }

    public static class FilterNotFoundException extends ClassNotFoundException {
        public FilterNotFoundException(final String message, final Exception exception) {
            super(message, exception);
        }
    }
}
