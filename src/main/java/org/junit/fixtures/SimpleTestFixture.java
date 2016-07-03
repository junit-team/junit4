package org.junit.fixtures;

/**
 * Base class for simple test fixtures.
 */
public abstract class SimpleTestFixture implements TestFixture {

    public final void initialize(FixtureContext context) throws Exception {
        beforeTest();
        context.addTearDown(new TearDown() {
            public void tearDown() throws Exception {
                afterTest();
            }
        });
        context.addTestPostcondition(new TestPostcondition() {
            public void verify() throws Exception {
                SimpleTestFixture.this.verify();
            }
        });
    }
 
    /**
     * Override to set up your test fixture before the test starts.
     *
     * @throws Exception if setup fails (which will disable {@link #afterTest()})
     */
    protected void beforeTest() throws Exception {
    }
 
    /**
     * Verifies any postconditions on the test fixture. Only called if the test
     * passes, and if no previous postcondition fails (which means it is not
     * called if {@link #beforeTest()} throws an exception).
     *
     * @throws Exception if verification fails (which will cause the test method to fail)
     */
    protected void verify() throws Exception {
    }
    
    /**
     * Override to tear down your test fixture after the test run completes.
     * This will be called whether the test passes or fails, but will not
     * be called unless {@link #beforeTest()} is called and doesn't throw
     * an exception.
     *
     * @throws Exception if setup fails (which will cause the test method to fail)
     */
    protected void afterTest() throws Exception {
    }
}
