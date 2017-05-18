package org.junit.tests.manipulation;

import org.junit.runner.manipulation.Ordering;

/**
 * An ordering that orders tests reverse alphanumerically by test name.
 */
public final class ReverseAlphanumericOrdering extends ComparsionBasedOrdering
        implements Ordering.Factory {

    public ReverseAlphanumericOrdering() {
        super(Comparators.reverse(Comparators.alphanumeric()));
    }

    public Ordering create(Context context) {
        return this;
    }
}
