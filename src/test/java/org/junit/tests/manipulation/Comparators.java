package org.junit.tests.manipulation;

import java.util.Comparator;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Alphanumeric;

/**
 * Factory and utility metods for creating {@link Comparator} instances for tests.
 */
class Comparators {
    private static final Comparator<Description> ALPHANUMERIC  = new Alphanumeric();

    private Comparators() {}
 
    public static Comparator<Description> alphanumeric() {
        return ALPHANUMERIC;
    }
 
    public static Comparator<Description> reverse(final Comparator<Description> comparator) {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return ALPHANUMERIC.compare(o2, o1);
            }
        };
    }
}
