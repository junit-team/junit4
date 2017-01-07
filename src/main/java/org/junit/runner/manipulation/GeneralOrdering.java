package org.junit.runner.manipulation;

import java.util.Collection;
import java.util.List;

import org.junit.runner.Description;

/**
 * An {@link Ordering} that is not a {@link Sorter}.
 *
 * @since 4.13
 */
public final class GeneralOrdering extends Ordering {
    private final Ordering delegate;

    GeneralOrdering(Ordering delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Description> orderItems(Collection<Description> descriptions) {
        return delegate.orderItems(descriptions);
    }

    @Override
    public void apply(Object target) throws InvalidOrderingException {
        /*
         * We overwrite apply() to avoid having a GeneralOrdering wrap another
         * GeneralOrdering.
         */
        if (target instanceof Orderable) {
            Orderable orderable = (Orderable) target;
            orderable.order(this);
        }
    }

    @Override
    boolean validateOrderingIsCorrect() {
        return delegate.validateOrderingIsCorrect();
    }
}
