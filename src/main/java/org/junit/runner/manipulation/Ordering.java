package org.junit.runner.manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
     * Creates an {@link Ordering} from the given class. The class must have a public no-argument constructor.
     *
     * @throws InvalidOrderingException if the instance could not be created
     */
    public static Ordering definedBy(Class<? extends Ordering> orderingClass)
            throws InvalidOrderingException {
        try {
            return orderingClass.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidOrderingException("Could not create ordering", e);
        } catch (IllegalAccessException e) {
            throw new InvalidOrderingException("Could not create ordering", e);
        }
    }

    /**
     * Order the tests in <code>runner</code> using this ordering.
     *
     * @throws InvalidOrderingException if ordering does something invalid (like remove or add children)
     */
    public void apply(Object runner) throws InvalidOrderingException {
        if (runner instanceof Orderable) {
            Orderable orderable = (Orderable) runner;
            orderable.order(new GenericOrdering(this));
        }
    }

    /**
     * Orders the given descriptions (all of which have the same parent).
     *
     * @param siblings unmodifiable collection of descriptions to order
     */
    public abstract List<Description> order(Collection<Description> siblings);
}
