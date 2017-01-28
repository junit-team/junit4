package org.junit.fixtures;

/**
 * An assertion that is run after the test successfully completes.
 */
public interface TestPostcondition {

    /**
     * Runs assertions that should be run after the test successfully completes,
     * but before the test status has been reported. If an assertion fails, this
     * method can indicate it by throwing an exception (generally a subclass of
     * {@link AssertionError} is used, but that is not required).
     */
    void verify() throws Exception;
}
