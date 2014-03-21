package junit.extensions;

import junit.framework.Test;
import junit.framework.TestResult;

/**
 * A Decorator that runs a test repeatedly.
 */
public class RepeatedTest extends TestDecorator {
    private int timesRepeat;

    public RepeatedTest(Test test, int repeat) {
        super(test);
        if (repeat < 0) {
            throw new IllegalArgumentException("Repetition count must be >= 0");
        }
        timesRepeat = repeat;
    }

    @Override
    public int countTestCases() {
        return super.countTestCases() * timesRepeat;
    }

    @Override
    public void run(TestResult result) {
        for (int i = 0; i < timesRepeat; i++) {
            if (result.shouldStop()) {
                break;
            }
            super.run(result);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "(repeated)";
    }
}