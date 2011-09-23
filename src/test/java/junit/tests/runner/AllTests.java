package junit.tests.runner;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests
 *
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() { // Collect tests manually because we have to test class collection code
		TestSuite suite= new TestSuite("Framework Tests");
		suite.addTestSuite(StackFilterTest.class);
		suite.addTestSuite(ResultTest.class);
		suite.addTestSuite(BaseTestRunnerTest.class);
		suite.addTestSuite(TextFeedbackTest.class);
		suite.addTestSuite(TextRunnerSingleMethodTest.class);
		suite.addTestSuite(TextRunnerTest.class);
		return suite;
	}
	
	static boolean isJDK11() {
		String version= System.getProperty("java.version");
		return version.startsWith("1.1");
	}
}
