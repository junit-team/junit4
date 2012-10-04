package junit.tests.framework;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Framework Tests");
        suite.addTestSuite(TestCaseTest.class);
        suite.addTest(SuiteTest.suite()); // Tests suite building, so can't use automatic test extraction
        suite.addTestSuite(TestListenerTest.class);
        suite.addTestSuite(AssertionFailedErrorTest.class);
        suite.addTestSuite(AssertTest.class);
        suite.addTestSuite(TestImplementorTest.class);
        suite.addTestSuite(NoArgTestCaseTest.class);
        suite.addTestSuite(ComparisonCompactorTest.class);
        suite.addTestSuite(ComparisonFailureTest.class);
        suite.addTestSuite(DoublePrecisionAssertTest.class);
        suite.addTestSuite(FloatAssertTest.class);
        return suite;
    }

}