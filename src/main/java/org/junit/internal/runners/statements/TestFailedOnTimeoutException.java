package org.junit.internal.runners.statements;

public class TestFailedOnTimeoutException extends Exception {

    private static final long serialVersionUID = 31935685163547539L;

    public TestFailedOnTimeoutException(String message) {
        super(message);
    }

}
