package org.junit.tests.manipulation;

/**
 * An ordering that orders tests alphanumerically by test name.
 */
public class AlphanumericOrdering extends ComparsionBasedOrdering {

    public AlphanumericOrdering() {
        super(Comparators.alphanumeric());
    }
}
