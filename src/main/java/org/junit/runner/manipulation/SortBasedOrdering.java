package org.junit.runner.manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Description;

class SortBasedOrdering extends Ordering {
    private final Sorter sorter;

    public SortBasedOrdering(Sorter sorter) {
        this.sorter = sorter;
    }

    @Override
    public void apply(Object runner)  {
        if (runner instanceof Sortable) {
            // Sorting is more efficient than ordering, so apply the sorter.
            sorter.apply(runner);
            return;
        }

        try {
            super.apply(runner);
        } catch (InvalidOrderingException e) {
            throw new AssertionError("SortBasedOrdering should always produce a valid ordering");
        }
    }

    @Override
    public List<Description> order(Collection<Description> siblings) {
        List<Description> sorted = new ArrayList<Description>(siblings);
        Collections.sort(sorted, sorter);
        return sorted;
    }
}
