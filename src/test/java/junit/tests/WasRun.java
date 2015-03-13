package junit.tests;

import junit.framework.TestCase;

/**
 * A helper test case for testing whether the testing method
 * is run.
 */
public class WasRun extends TestCase {
    public boolean fWasRun = Boolean.FALSE;

    @Override
    protected void runTest() {
        fWasRun = Boolean.TRUE;
    }
}