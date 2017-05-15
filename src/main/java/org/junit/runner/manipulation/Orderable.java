package org.junit.runner.manipulation;


/**
 * Interface for runners that allow ordering of tests.
 *
 * <p>Beware of using this interface to cope with order dependencies between tests.
 * Tests that are isolated from each other are less expensive to maintain and
 * can be run individually.
 *
 * @since 4.13
 */
public interface Orderable extends Sortable {

    /**
     * Orders the tests using <code>ordering</code>
     *
     * @throws InvalidOrderingException if ordering does something invalid (like remove or add
     * children)
     */
    void order(GeneralOrdering ordering) throws InvalidOrderingException;
}
