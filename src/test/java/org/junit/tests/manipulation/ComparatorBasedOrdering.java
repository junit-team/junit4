package org.junit.tests.manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Ordering;

/**
 * An ordering that internally uses a {@link Comparator}.
 */
class ComparatorBasedOrdering extends Ordering {
    private final Comparator<Description> comparator;

    protected ComparatorBasedOrdering(Comparator<Description> comparator) {
        this.comparator = comparator;
    }

    @Override
    protected List<Description> orderItems(Collection<Description> descriptions) {
        List<Description> ordered = new ArrayList<Description>(descriptions);
        Collections.sort(ordered, comparator);
        return ordered;
    }
}
