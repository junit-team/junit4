package junit.tests;

import junit.framework.*;
import junit.runner.BaseTestRunner;

/**
 * TestSuite that runs all the sample tests
 *
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
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
		suite.addTestSuite(RepeatedTestTest.class);
		suite.addTestSuite(TestImplementorTest.class);
		if (!BaseTestRunner.inVAJava()) {
			suite.addTestSuite(TextRunnerTest.class);
			if (!isJDK11())
				suite.addTest(new TestSuite(TestCaseClassLoaderTest.class));
		}
		return suite;
	}
	
	static boolean isJDK11() {
		String version= System.getProperty("java.version");
		return version.startsWith("1.1");
	}
}