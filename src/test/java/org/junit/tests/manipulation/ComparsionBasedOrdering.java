package org.junit.tests.manipulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Ordering;

/**
 * An ordering that internally uses a {@link Comparator}.
 */
class ComparsionBasedOrdering extends Ordering {
    private final Comparator<Description> comparator;

    public ComparsionBasedOrdering(Comparator<Description> comparator) {
        this.comparator = comparator;
    }

    @Override
    public List<Description> orderChildren(Description parent) {
        List<Description> ordered = new ArrayList<Description>(parent.getChildren());
        Collections.sort(ordered, comparator);
        return ordered;
    }
}
