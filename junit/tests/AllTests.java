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
		suite.addTest(new TestSuite(ExtensionTest.class));
	    suite.addTest(new TestSuite(TestCaseTest.class));
	    suite.addTest(SuiteTest.suite()); // Tests suite building, so can't use automatic test extraction 
		suite.addTest(new TestSuite(ExceptionTestCaseTest.class));
		suite.addTest(new TestSuite(TestListenerTest.class));
		suite.addTest(new TestSuite(ActiveTestTest.class));
		suite.addTest(new TestSuite(AssertTest.class));
		suite.addTest(new TestSuite(TextRunnerTest.class));
		
		if (!BaseTestRunner.inVAJava())
			suite.addTest(new TestSuite(TestTestCaseClassLoader.class));
	    return suite;
	}
}