package org.junit.tests.manipulation;

import org.junit.runner.manipulation.Ordering;

/**
 * An ordering that orders tests alphanumerically by test name.
 */
public final class AlphanumericOrdering implements Ordering.Factory {
    public static final ComparatorBasedOrdering INSTANCE = new ComparatorBasedOrdering(
            Comparators.alphanumeric());

    public Ordering create(Ordering.Context context) {
        return INSTANCE;
    }
}
