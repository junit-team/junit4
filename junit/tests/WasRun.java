package junit.tests;

import junit.framework.TestCase;

/**
 * A helper test case for testing whether the testing method
 * is run.
 */
public class WasRun extends TestCase {
	public boolean fWasRun= false;
		protected void runTest() {
			fWasRun= true;
		}
}