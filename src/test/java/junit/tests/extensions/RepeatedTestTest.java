package junit.tests.extensions;

import junit.extensions.RepeatedTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Testing the RepeatedTest support.
 */
public class RepeatedTestTest extends TestCase {
    private TestSuite suite;

    public static class SuccessTest extends TestCase {

        @Override
        public void runTest() {
        }
    }

    public RepeatedTestTest(String name) {
        super(name);
        suite = new TestSuite();
        suite.addTest(new SuccessTest());
        suite.addTest(new SuccessTest());
    }

    public void testRepeatedOnce() {
        Test test = new RepeatedTest(suite, 1);
        assertEquals(2, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        assertEquals(2, result.runCount());
    }

    public void testRepeatedMoreThanOnce() {
        Test test = new RepeatedTest(suite, 3);
        assertEquals(6, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        assertEquals(6, result.runCount());
    }

    public void testRepeatedZero() {
        Test test = new RepeatedTest(suite, 0);
        assertEquals(0, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        assertEquals(0, result.runCount());
    }

    public void testRepeatedNegative() {
        try {
            new RepeatedTest(suite, -1);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(">="));
            return;
        }
        fail("Should throw an IllegalArgumentException");
    }
}