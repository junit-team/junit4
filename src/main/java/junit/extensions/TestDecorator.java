package junit.extensions;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.IntermediateTest;

/**
 * A Decorator for Tests. Use TestDecorator as the base class for defining new
 * test decorators. Test decorator subclasses can be introduced to add behaviour
 * before or after a test is run.
 */
@SuppressWarnings("deprecation")
public class TestDecorator extends Assert implements Test, IntermediateTest{
    protected Test fTest;

    public TestDecorator(Test test) {
        fTest = test;
    }

    /**
     * The basic run behaviour.
     */
    @Override
    public void basicRun(TestResult result) {
        fTest.run(result);
    }

    @Override
    public int countTestCases() {
        return fTest.countTestCases();
    }

    @Override
    public void run(TestResult result) {
        basicRun(result);
    }

    @Override
    public String toString() {
        return fTest.toString();
    }

    public Test getTest() {
        return fTest;
    }
}