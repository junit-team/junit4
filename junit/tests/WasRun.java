package junit.tests;

import junit.framework.*;

/**
 * A helper test case for testing whether the testing method
 * is run.
 */
public class WasRun extends TestCase {
	public boolean fWasRun= false;
		public WasRun(String name) {
			super(name);
		}
		protected void runTest() {
			fWasRun= true;
		}
}