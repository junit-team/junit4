package org.junit.tests.manipulation;

import org.junit.runner.manipulation.Ordering;
import org.junit.runner.manipulation.Sorter;

/**
 * A sorter that orders tests alphanumerically by test name.
 */
public class AlphanumericSorter extends Sorter {

    public AlphanumericSorter(Ordering.Context context) {
        super(Comparators.alphanumeric());
    }
}
