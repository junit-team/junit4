package org.junit.runner.manipulation;

import java.util.Comparator;

import org.junit.runner.Description;

/**
 * A <code>Sorter</code> orders tests. In general you will not need
 * to use a <code>Sorter</code> directly. Instead, use {@link org.junit.runner.Request#sortWith(Comparator)}.
 *
 * @since 4.0
 */
public class Sorter implements Comparator<Description> {
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
     * Sorts the test in <code>runner</code> using <code>comparator</code>
     */
    public void apply(Object object) {
        if (object instanceof Sortable) {
            Sortable sortable = (Sortable) object;
            sortable.sort(this);
        }
    }

    public int compare(Description o1, Description o2) {
        return comparator.compare(o1, o2);
    }
}
