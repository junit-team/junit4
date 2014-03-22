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
    private TestResult fResult;
    private int fStartCount;
    private int fEndCount;
    private int fFailureCount;
    private int fErrorCount;

    public void addError(Test test, Throwable e) {
        fErrorCount++;
    }

    public void addFailure(Test test, AssertionFailedError t) {
        fFailureCount++;
    }

    public void endTest(Test test) {
        fEndCount++;
    }

    @Override
    protected void setUp() {
        fResult = new TestResult();
        fResult.addListener(this);

        fStartCount = 0;
        fEndCount = 0;
        fFailureCount = 0;
        fErrorCount = 0;
    }

    public void startTest(Test test) {
        fStartCount++;
    }

    public void testError() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
                throw new Error();
            }
        };
        test.run(fResult);
        assertEquals(1, fErrorCount);
        assertEquals(1, fEndCount);
    }

    public void testFailure() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
                fail();
            }
        };
        test.run(fResult);
        assertEquals(1, fFailureCount);
        assertEquals(1, fEndCount);
    }

    public void testStartStop() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
            }
        };
        test.run(fResult);
        assertEquals(1, fStartCount);
        assertEquals(1, fEndCount);
    }
}