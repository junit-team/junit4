package junit.extensions;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 * A Decorator for Tests. Use TestDecorator as the base class for defining new
 * test decorators. Test decorator subclasses can be introduced to add behaviour
 * before or after a test is run.
 */
public class TestDecorator extends Assert implements Test {
    protected Test test;

    public TestDecorator(Test test) {
        this.test = test;
    }

    /**
     * The basic run behaviour.
     */
    public void basicRun(TestResult result) {
        test.run(result);
    }

    public int countTestCases() {
        return test.countTestCases();
    }

    public void run(TestResult result) {
        basicRun(result);
    }

    @Override
    public String toString() {
        return test.toString();
    }

    public Test getTest() {
        return test;
    }
}