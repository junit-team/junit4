package junit.framework;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * A <code>TestFailure</code> collects a failed test together with
 * the caught exception.
 *
 * @see TestResult
 */
public class TestFailure {
    protected Test failedTest;
    protected Throwable thrownException;


    /**
     * Constructs a TestFailure with the given test and exception.
     */
    public TestFailure(Test failedTest, Throwable thrownException) {
        this.failedTest = failedTest;
        this.thrownException = thrownException;
    }

    /**
     * Gets the failed test.
     */
    public Test failedTest() {
        return failedTest;
    }

    /**
     * Gets the thrown exception.
     */
    public Throwable thrownException() {
        return thrownException;
    }

    /**
     * Returns a short description of the failure.
     */
    @Override
    public String toString() {
        return failedTest + ": " + thrownException.getMessage();
    }

    public String trace() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        thrownException().printStackTrace(writer);
        return stringWriter.toString();
    }

    public String exceptionMessage() {
        return thrownException().getMessage();
    }

    public boolean isFailure() {
        return thrownException() instanceof AssertionFailedError;
    }
}