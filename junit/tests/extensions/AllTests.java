package junit.tests.extensions;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the extension tests
 *
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() { // Collect tests manually because we have to test class collection code
		TestSuite suite= new TestSuite("Framework Tests");
		suite.addTestSuite(ExtensionTest.class);
		suite.addTestSuite(ExceptionTestCaseTest.class);
		suite.addTestSuite(ActiveTestTest.class);
		suite.addTestSuite(RepeatedTestTest.class);
		return suite;
	}
}