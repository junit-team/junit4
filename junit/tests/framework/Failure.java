package junit.tests.framework;

import junit.framework.*;

/**
 * A test case testing the testing framework.
 *
 */
public class Failure extends TestCase {
	public void runTest() {
		fail();
	}
}