package junit.tests.framework;

import java.util.Vector;
import junit.framework.*;

/**
 * A test case testing the testing framework.
 *
 */
public class Failure extends TestCase {
	
	public Failure(String name) {
		super(name);
	}
	public void test() {
		fail();
	}
}