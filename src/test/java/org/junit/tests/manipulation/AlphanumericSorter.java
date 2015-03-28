package org.junit.tests.manipulation;

import org.junit.runner.manipulation.Sorter;

/**
 * A sorter that orders tests alphanumerically by test name.
 */
public class AlphanumericSorter extends Sorter {

    public AlphanumericSorter() {
        super(Comparators.alphanumeric());
    }
}
