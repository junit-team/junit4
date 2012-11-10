package junit.tests.framework;

import junit.framework.Protectable;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Test an implementor of junit.framework.Test other than TestCase or TestSuite
 */
public class TestImplementorTest extends TestCase {
    public static class DoubleTestCase implements Test {
        private TestCase fTestCase;

        public DoubleTestCase(TestCase testCase) {
            fTestCase = testCase;
        }

        public int countTestCases() {
            return 2;
        }

        public void run(TestResult result) {
            result.startTest(this);
            Protectable p = new Protectable() {
                public void protect() throws Throwable {
                    fTestCase.runBare();
                    fTestCase.runBare();
                }
            };
            result.runProtected(this, p);
            result.endTest(this);
        }
    }

    private DoubleTestCase fTest;

    public TestImplementorTest() {
        TestCase testCase = new TestCase() {
            @Override
            public void runTest() {
            }
        };
        fTest = new DoubleTestCase(testCase);
    }

    public void testSuccessfulRun() {
        TestResult result = new TestResult();
        fTest.run(result);
        assertEquals(fTest.countTestCases(), result.runCount());
        assertEquals(0, result.errorCount());
        assertEquals(0, result.failureCount());
    }
}
