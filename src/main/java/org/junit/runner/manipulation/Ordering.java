package org.junit.runner.manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.junit.runner.Description;

/**
 * Reorders tests. An {@code Ordering} can reverse the order of tests, sort the
 * order or even shuffle the order.
 * 
 * <p>In general you will not need to use a <code>Ordering</code> directly.
 * Instead, use {@link org.junit.runner.Request#orderWith(Ordering)}.
 *
 * @since 4.13
 */
public abstract class Ordering {

    /**
     * Creates an {@link Ordering} that orders the items using the given
     * {@link Comparator}.
     */
    public static Ordering sortedBy(Comparator<Description> comparator) {
        return sortedBy(new Sorter(comparator));
    }
    
    static Ordering sortedBy(final Sorter sorter) {
        return new Ordering() {
            @Override
            public List<Description> order(Collection<Description> siblings) {
                List<Description> sorted = new ArrayList<Description>(siblings);
                Collections.sort(sorted, sorter);
                return sorted;
            }

            @Override
            public void apply(Object runner) {
                if (runner instanceof Sortable) {
                    Sortable sortable = (Sortable) runner;
                    sortable.sort(sorter);
                } else {
                    super.apply(runner);
                }
            }
        };
    }

    /**
     * Creates an {@link Ordering} that shuffles the items using the given
     * {@link Random} instance.
     */
    public static Ordering shuffledBy(final Random random) {
        return new Ordering() {
            @Override
            public List<Description> order(Collection<Description> siblings) {
                List<Description> shuffled = new ArrayList<Description>(siblings);
                Collections.shuffle(shuffled, random);
                return shuffled;
            }
        };
    }

    /**
     * Order the tests in <code>runner</code> using this ordering.
     */
    public void apply(Object runner) {
        if (runner instanceof Orderable) {
            Orderable orderable = (Orderable) runner;
            orderable.order(this);
        }
    }

    /**
     * Orders the given descriptions (all of which have the same parent).
     *
     * @param siblings unmodifiable collection of descriptions to order
     */
    public abstract List<Description> order(Collection<Description> siblings);
}
