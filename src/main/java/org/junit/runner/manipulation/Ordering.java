package org.junit.runner.manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.OrderWith;

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
            boolean validateOrderingIsCorrect() {
                return false;
            }

            @Override
            protected List<Description> orderItems(Collection<Description> descriptions) {
                List<Description> shuffled = new ArrayList<Description>(descriptions);
                Collections.shuffle(shuffled, random);
                return shuffled;
            }
        };
    }

    /**
     * Creates an {@link Ordering} from the given class. The class must have a public constructor
     * that takes in an {@link Ordering.Context}.
     *
     * @param orderingClass class to use to create the ordering
     * @param annotatedTestClass test class that is annotated with {@link OrderWith}.
     * @throws InvalidOrderingException if the instance could not be created
     */
    public static Ordering definedBy(
            Class<? extends Ordering> orderingClass, Description annotatedTestClass)
            throws InvalidOrderingException {
        Ordering.Context context = new Ordering.Context(annotatedTestClass);
        try {
            return orderingClass.getConstructor(Ordering.Context.class).newInstance(context);
        } catch (ReflectiveOperationException e) {
            throw new InvalidOrderingException(
                    "Could not create ordering for " + annotatedTestClass, e);
        }
    }

    /**
     * Order the tests in <code>target</code> using this ordering.
     *
     * @throws InvalidOrderingException if ordering does something invalid (like remove or add children)
     */
    public void apply(Object target) throws InvalidOrderingException {
        /*
         * Note that some subclasses of Ordering override apply(). The Sorter
         * subclass of Ordering overrides apply() to apply the sort (this is
         * done because sorting is more efficient than ordering) the
         * GeneralOrdering overrides apply() to avoid having a GenericOrdering
         * wrap another GenericOrdering.
         */
        if (target instanceof Orderable) {
            Orderable orderable = (Orderable) target;
            orderable.order(new GeneralOrdering(this));
        }
    }

    boolean validateOrderingIsCorrect() {
        return true;
    }

    /**
     * Orders the descriptions.
     *
     * @return descriptions in order
     */
    public final List<Description> order(Collection<Description> descriptions) throws InvalidOrderingException {
        List<Description> inOrder = orderItems(Collections.unmodifiableCollection(descriptions));
        if (!validateOrderingIsCorrect()) {
            return inOrder;
        }

        Set<Description> uniqueDescriptions = new HashSet<Description>(descriptions);
        if (!uniqueDescriptions.containsAll(inOrder)) {
            throw new InvalidOrderingException("Ordering added items");
        }
        Set<Description> resultAsSet = new HashSet<Description>(inOrder);
        if (resultAsSet.size() != inOrder.size()) {
            throw new InvalidOrderingException("Ordering duplicated items");
        } else if (!resultAsSet.containsAll(uniqueDescriptions)) {
            throw new InvalidOrderingException("Ordering removed items");
        }

        return inOrder;
    }
 
    /**
     * Implemented by sub-classes to order the descriptions.
     *
     * @return descriptions in order
     */
    protected abstract List<Description> orderItems(Collection<Description> descriptions);

    /** Context about the ordering being applied. */
    public static class Context {
        private final Description description;

        /**
         * Gets the description for the top-level target being ordered.
         */
        public Description getTarget() {
            return description;
        }

        private Context(Description description) {
            this.description = description;
        }
    }
}
