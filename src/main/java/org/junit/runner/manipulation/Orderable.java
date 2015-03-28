package org.junit.runner.manipulation;

/**
 * Interface for runners that allow ordering of tests. Note that any runner that
 * is {@code Orderable} can also be sorted as if it implemented {@link Sortable}.
 *
 * <p>Beware of using this interface to cope with order dependencies between tests.
 * Tests that are isolated from each other are less expensive to maintain and
 * can be run individually.
 *
 * @since 4.13
 */
public interface Orderable {

    /**
     * Orders the tests using <code>ordering</code>
     */
    void order(Ordering ordering);
}
