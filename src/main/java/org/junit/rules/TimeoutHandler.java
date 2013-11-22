package org.junit.rules;

public interface TimeoutHandler {

    /**
     * If the test runs into test timeout, this callback will be called allowing to gather
     * additional test failure context (e.g. of other involved systems).
     *
     * @param thread the actual test executing main thread
     * @return the optional exception will be added to the test failure
     */
    Exception handleTimeout(Thread thread);

}
