package org.junit.fixtures;

/**
 * Provides methods to register {@link TearDown} and {@link TestPostcondition} instances.
 */
public abstract class FixtureContext {

    /**
     * Adds a {@link TearDown} to run after the test completes.
     */
    abstract public void addTearDown(TearDown tearDown);
 
    /**
     * Adds a {@link TestPostcondition} to run after a successful test run. The 
     * post-condition will run before any {@link TearDown} instances, but
     * may not run at all if an easier post-condidtion fails.
     */
    public abstract void addTestPostcondition(TestPostcondition postcondition);
}
