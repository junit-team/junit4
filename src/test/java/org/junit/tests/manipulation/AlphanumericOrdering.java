package org.junit.tests.manipulation;

import org.junit.runner.manipulation.Ordering;

/**
 * An ordering that orders tests alphanumerically by test name.
 */
public class AlphanumericOrdering extends ComparsionBasedOrdering {

    public AlphanumericOrdering() {
        super(Comparators.alphanumeric());
    }

    public AlphanumericOrdering(Ordering.Context context) {
        this();
    }
}
