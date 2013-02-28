package org.junit.runner;

import org.junit.runner.manipulation.Filter;

public class FilterFactoryFactory {
    public Filter apply(final String filterSpec)
            throws FilterFactoryNotFoundException, FilterFactory.FilterNotFoundException {
        if (filterSpec.contains("=")) {
            final String[] tuple = filterSpec.split("=", 2);

            final FilterFactory filterFactory = create(tuple[0]);
            final String args = tuple[1];

            return filterFactory.createFilter(args);
        } else {
            final FilterFactory filterFactory = create(filterSpec);

            return filterFactory.createFilter();
        }
    }

    private FilterFactory create(final String filterFactoryFqcn) throws FilterFactoryNotFoundException {
        try {
            final Class<? extends FilterFactory> filterFactoryClass =
                    (Class<? extends FilterFactory>) Class.forName(filterFactoryFqcn);

            return filterFactoryClass.getConstructor().newInstance();
        } catch (final Exception e) {
            throw new FilterFactoryNotFoundException(e.getMessage());
        }
    }

    public static class FilterFactoryNotFoundException extends ClassNotFoundException {
        public FilterFactoryNotFoundException(final String message) {
            super(message);
        }
    }
}
