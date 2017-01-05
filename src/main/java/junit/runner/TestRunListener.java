package junit.runner;

/**
 * A listener interface for observing the
 * execution of a test run. Unlike TestListener,
 * this interface using only primitive objects,
 * making it suitable for remote test execution.
 */
public interface TestRunListener {
    /* test status constants*/
    int STATUS_ERROR = 1;
    int STATUS_FAILURE = 2;

    void testRunStarted(String testSuiteName, int testCount);

    void testRunEnded(long elapsedTime);

    void testRunStopped(long elapsedTime);

    void testStarted(String testName);

    void testEnded(String testName);

    void testFailed(int status, String testName, String trace);
}
