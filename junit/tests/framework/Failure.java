package junit.tests.framework;

import junit.framework.TestCase;

/**
 * A test case testing the testing framework.
 *
 */
public class Failure extends TestCase {
	public void runTest() {
		fail();
	}
}