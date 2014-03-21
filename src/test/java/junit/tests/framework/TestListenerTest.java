package junit.tests.framework;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;

/**
 * Test class used in SuiteTest
 */
public class TestListenerTest extends TestCase implements TestListener {
    private TestResult result;
    private int startCount;
    private int endCount;
    private int failureCount;
    private int errorCount;

    public void addError(Test test, Throwable e) {
        errorCount++;
    }

    public void addFailure(Test test, AssertionFailedError t) {
        failureCount++;
    }

    public void endTest(Test test) {
        endCount++;
    }

    @Override
    protected void setUp() {
        result = new TestResult();
        result.addListener(this);

        startCount = 0;
        endCount = 0;
        failureCount = 0;
        errorCount = 0;
    }

    public void startTest(Test test) {
        startCount++;
    }

    public void testError() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
                throw new Error();
            }
        };
        test.run(result);
        assertEquals(1, errorCount);
        assertEquals(1, endCount);
    }

    public void testFailure() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
                fail();
            }
        };
        test.run(result);
        assertEquals(1, failureCount);
        assertEquals(1, endCount);
    }

    public void testStartStop() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
            }
        };
        test.run(result);
        assertEquals(1, startCount);
        assertEquals(1, endCount);
    }
}