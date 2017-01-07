package org.junit.tests.manipulation;

import org.junit.runner.manipulation.Ordering;

/**
 * An ordering that orders tests reverse alphanumerically by test name.
 */
public class ReverseAlphanumericOrdering extends ComparsionBasedOrdering {

    public ReverseAlphanumericOrdering() {
        super(Comparators.reverse(Comparators.alphanumeric()));
    }

    public ReverseAlphanumericOrdering(Ordering.Context context) {
        this();
    }
}
