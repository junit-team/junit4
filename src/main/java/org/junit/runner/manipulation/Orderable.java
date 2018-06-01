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
     * Orders the tests using <code>orderer</code>
     *
     * @throws InvalidOrderingException if orderer does something invalid (like remove or add
     * children)
     */
    void order(Orderer orderer) throws InvalidOrderingException;
}
