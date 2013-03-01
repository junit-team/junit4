package org.junit.runner;

import org.junit.runner.manipulation.Filter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class FilterFactory {
    public Filter createFilter() throws FilterNotCreatedException {
        throw new NotImplementedException();
    }

    public Filter createFilter(String args) throws FilterNotCreatedException {
        throw new NotImplementedException();
    }

    public static class FilterNotCreatedException extends ClassNotFoundException {
        public FilterNotCreatedException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
