package org.junit.tests.manipulation;

/**
 * An ordering that orders tests reverse alphanumerically by test name.
 */
public class ReverseAlphanumericOrdering extends ComparsionBasedOrdering {

    public ReverseAlphanumericOrdering() {
        super(Comparators.reverse(Comparators.alphanumeric()));
    }
}
