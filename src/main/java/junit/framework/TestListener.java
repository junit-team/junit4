package junit.framework;

/**
 * A Listener for test progress
 */
public interface TestListener {
    /**
     * An error occurred.
     */
    public void addError(Test test, Throwable e);

    /**
     * A failure occurred.
     */
    public void addFailure(Test test, AssertionFailedError e);

    /**
     * A test ended.
     */
    public void endTest(Test test);

    /**
     * A test started.
     */
    public void startTest(Test test);
}