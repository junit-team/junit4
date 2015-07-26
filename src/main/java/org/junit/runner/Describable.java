package org.junit.runner;


/**
 * Represents an object that can describe itself
 *
 * @since 4.5
 */
public interface Describable {
    /**
     * @return a {@link Description} showing the tests to be run by the receiver
     */
    Description getDescription();
}