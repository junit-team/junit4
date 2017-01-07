package org.junit.tests.manipulation;

import org.junit.runner.manipulation.Ordering;
import org.junit.runner.manipulation.Sorter;

/**
 * A sorter that orders tests reverse alphanumerically by test name.
 */
public class ReverseAlphanumericSorter extends Sorter {

    public ReverseAlphanumericSorter(Ordering.Context context) {
        super(Comparators.reverse(Comparators.alphanumeric()));
    }
}
