package org.junit.runner;

import org.junit.runner.manipulation.Filter;

public class FilterFactory {
    public Filter createFilter(String filterSpec) throws FilterNotFoundException {
        try {
            if (filterSpec.contains("=")) {
                final String[] constructorAndArgs = filterSpec.split("=", 2);

                filterSpec = constructorAndArgs[0];
                final String args = constructorAndArgs[1];

                final Class<?> filterClass = Class.forName(filterSpec);
                return (Filter) filterClass.getConstructor(String.class).newInstance(args);
            } else {
                final Class<?> filterClass = Class.forName(filterSpec);
                return (Filter) filterClass.getConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new FilterNotFoundException(e.getMessage());
        }
    }

    public class FilterNotFoundException extends ClassNotFoundException {
        public FilterNotFoundException(final String message) {
            super(message);
        }
    }
}
