package org.junit.tests.manipulation;

import java.util.Comparator;

import org.junit.runner.Description;

/**
 * Factory and utility metods for creating {@link Comparator} instances for tests.
 */
class Comparators {

    private Comparators() {}
 
    public static Comparator<Description> alphanumeric() {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        };
    }
 
    public static Comparator<Description> reverse(final Comparator<Description> comparator) {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return comparator.compare(o2, o1);
            }
        };
    }
}
