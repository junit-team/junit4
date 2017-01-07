package org.junit.runner.manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
            boolean validateOrderingIsCorrect() {
                return false;
            }

            @Override
            protected List<Description> orderItems(Ordering.Context context, Collection<Description> descriptions) {
                List<Description> shuffled = new ArrayList<Description>(descriptions);
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
     * Order the tests in <code>target</code> using this ordering.
     *
     * @param runner the runner to apply the ordering to
     * @param context context for the ordering operation
     *
     * @throws InvalidOrderingException if ordering does something invalid (like remove or add children)
     */
    public void apply(Object target, Ordering.Context context)
            throws InvalidOrderingException {
        /*
         * Note that some subclasses of Ordering override applyOrdering(). The Sorter
         * subclass of Ordering overrides applyOrdering() to apply the sort (this is
         * done because sorting is more efficient than ordering) the
         * GeneralOrdering overrides applyOrdering() to avoid having a GenericOrdering
         * wrap another GenericOrdering.
         */
        if (target instanceof Orderable) {
            Orderable orderable = (Orderable) target;
            orderable.order(new GeneralOrdering(this), context);
        }
    }

    boolean validateOrderingIsCorrect() {
        return true;
    }

    /**
     * Orders the descriptions.
     *
     * @param context context for the ordering operation
     * @param descriptions items to sort
     * @return descriptions in order
     */
    public final List<Description> order(Ordering.Context context, Collection<Description> descriptions)
            throws InvalidOrderingException {
        List<Description> inOrder = orderItems(
                context, Collections.unmodifiableCollection(descriptions));
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
     * @param context context for the ordering operation
     * @param descriptions items to sort
     * @return descriptions in order
     */
    protected abstract List<Description> orderItems(Ordering.Context context, Collection<Description> descriptions);

    /** Context about the ordering being applied. */
    public final static class Context {
        private final Description description;

        /** Creates a builder for building this context. */
        public static Builder builder() { return new Builder(); }

        /**
         * Gets the description for the top-level target being ordered.
         */
        public Description getTarget() {
            return description;
        }

        private Context(Builder builder) {
            this.description = builder.description;
        }

        public static final class Builder {
            private Description description;

            public Builder withTarget(Description description) {
                this.description = description;
                return this;
            }

            public Context build() {
                if (description == null) {
                    throw new IllegalStateException("Must call withTarget() first");
                }
                return new Context(this);
            }
        }
    }
}
