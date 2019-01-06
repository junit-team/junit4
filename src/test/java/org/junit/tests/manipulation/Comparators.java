package org.junit.tests.manipulation;

import java.util.Comparator;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Alphanumeric;

/**
 * Factory and utility methods for creating {@link Comparator} instances for tests.
 */
class Comparators {
    private static final Comparator<Description> ALPHANUMERIC  = new Alphanumeric();

    private Comparators() {}
 
    public static Comparator<Description> alphanumeric() {
        return ALPHANUMERIC;
    }
}
