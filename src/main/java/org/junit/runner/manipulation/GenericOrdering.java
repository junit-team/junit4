package org.junit.runner.manipulation;

import java.util.List;

import org.junit.runner.Description;

/**
 * An {@link Ordering} that is not a {@link Sorter}.
 *
 * @since 4.13
 */
public final class GenericOrdering extends Ordering {
    private final Ordering delegate;

    GenericOrdering(Ordering delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Description> orderChildren(Description parent) {
        return delegate.orderChildren(parent);
    }

    @Override
    public void apply(Object runner) throws InvalidOrderingException {
        /*
         * We overwrite apply() to avoid having a GenericOrdering wrap another
         * GenericOrdering.
         */
        if (runner instanceof Orderable) {
            Orderable orderable = (Orderable) runner;
            orderable.order(this);
        }
    }
}
