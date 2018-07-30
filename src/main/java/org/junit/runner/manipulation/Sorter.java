package org.junit.runner.manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runner.Description;

/**
 * A <code>Sorter</code> orders tests. In general you will not need
 * to use a <code>Sorter</code> directly. Instead, use
 * {@link org.junit.runner.Request#sortWith(Comparator)}.
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
     * @since 4.0
     */
    public Sorter(Comparator<Description> comparator) {
        this.comparator = comparator;
    }

    /**
     * Sorts the tests in <code>target</code> using <code>comparator</code>.
     *
     * @since 4.0
     */
    @Override
    public void apply(Object target) {
        /*
         * Note that all runners that are Orderable are also Sortable (because
         * Orderable extends Sortable). Sorting is more efficient than ordering,
         * so we override the parent behavior so we sort instead.
         */
        if (target instanceof Sortable) {
            Sortable sortable = (Sortable) target;
            sortable.sort(this);
        }
    }

    public int compare(Description o1, Description o2) {
        return comparator.compare(o1, o2);
    }
 
    /**
     * {@inheritDoc}
     *
     * @since 4.13
     */
    @Override
    protected final List<Description> orderItems(Collection<Description> descriptions) {
        /*
         * In practice, we will never get here--Sorters do their work in the
         * compare() method--but the Liskov substitution principle demands that
         * we obey the general contract of Orderable. Luckily, it's trivial to
         * implement.
         */
        List<Description> sorted = new ArrayList<Description>(descriptions);
        Collections.sort(sorted, this); // Note: it would be incorrect to pass in "comparator"
        return sorted;
    }

    /**
     * {@inheritDoc}
     *
     * @since 4.13
     */
    @Override
    boolean validateOrderingIsCorrect() {
        return false;
    }
}
