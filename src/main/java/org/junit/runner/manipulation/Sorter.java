package org.junit.runner.manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runner.Description;

/**
 * A <code>Sorter</code> orders tests. In general you will not need
 * to use a <code>Sorter</code> directly. Instead, use {@link org.junit.runner.Request#sortWith(Comparator)}.
 *
 * @since 4.0
 */
public class Sorter extends Ordering implements Comparator<Description> {
    /**
     * NULL is a <code>Sorter</code> that leaves elements in an undefined order
     */
    public static final Sorter NULL = new Sorter(new Comparator<Description>() {
        public int compare(Description o1, Description o2) {
            return 0;
        }
    });

    private final Comparator<Description> comparator;

    /**
     * Creates a <code>Sorter</code> that uses <code>comparator</code>
     * to sort tests
     *
     * @param comparator the {@link Comparator} to use when sorting tests
     */
    public Sorter(Comparator<Description> comparator) {
        this.comparator = comparator;
    }

    /**
     * Sorts the test in <code>runner</code> using <code>comparator</code>.
     */
    @Override
    public void apply(Object runner) {
        /*
         * Note that all runners that are Orderable are also Sortable (because
         * Orderable extends Sortable). Sorting is more efficient than ordering,
         * so we override the parent behavior so we sort instead.
         */
        if (runner instanceof Sortable) {
            Sortable sortable = (Sortable) runner;
            sortable.sort(this);
        }
    }

    public int compare(Description o1, Description o2) {
        return comparator.compare(o1, o2);
    }
 
    @Override
    public final List<Description> order(Collection<Description> siblings) {
        List<Description> sorted = new ArrayList<Description>(siblings);
        Collections.sort(sorted, this);
        return sorted;
    }
}
