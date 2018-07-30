package org.junit.tests.manipulation;

import static java.util.Collections.reverseOrder;

import org.junit.runner.manipulation.Ordering;

/**
 * An ordering that orders tests reverse alphanumerically by test name.
 */
public final class ReverseAlphanumericOrdering extends ComparatorBasedOrdering
        implements Ordering.Factory {

    public ReverseAlphanumericOrdering() {
        super(reverseOrder(Comparators.alphanumeric()));
    }

    public Ordering create(Context context) {
        return this;
    }
}
