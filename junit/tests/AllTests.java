package junit.tests;

import junit.framework.*;
import junit.runner.BaseTestRunner;

/**
 * TestSuite that runs all the sample tests
 *
 */
public class AllTests {

	public static void main (String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	public static Test suite ( ) {
		TestSuite suite= new TestSuite("Framework Tests");
		suite.addTestSuite(ExtensionTest.class);
		suite.addTestSuite(TestCaseTest.class);
		suite.addTest(SuiteTest.suite()); // Tests suite building, so can't use automatic test extraction 
		suite.addTestSuite(ExceptionTestCaseTest.class);
		suite.addTestSuite(TestListenerTest.class);
		suite.addTestSuite(ActiveTestTest.class);
		suite.addTestSuite(AssertTest.class);
		suite.addTestSuite(StackFilterTest.class);
		suite.addTestSuite(SorterTest.class);
		if (!BaseTestRunner.inVAJava()) {
			suite.addTestSuite(TextRunnerTest.class);
			suite.addTest(new TestSuite(TestTestCaseClassLoader.class));
		}
		return suite;
	}
}