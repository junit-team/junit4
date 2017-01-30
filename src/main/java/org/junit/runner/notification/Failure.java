package org.junit.runner.notification;

import java.io.Serializable;

import org.junit.internal.Throwables;
import org.junit.runner.Description;

/**
 * A <code>Failure</code> holds a description of the failed test and the
 * exception that was thrown while running it. In most cases the {@link org.junit.runner.Description}
 * will be of a single test. However, if problems are encountered while constructing the
 * test (for example, if a {@link org.junit.BeforeClass} method is not static), it may describe
 * something other than a single test.
 *
 * @since 4.0
 */
public class Failure implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * We have to use the f prefix until the next major release to ensure
     * serialization compatibility. 
     * See https://github.com/junit-team/junit4/issues/976
     */
    private final Description fDescription;
    private final Throwable fThrownException;

    /**
     * Constructs a <code>Failure</code> with the given description and exception.
     *
     * @param description a {@link org.junit.runner.Description} of the test that failed
     * @param thrownException the exception that was thrown while running the test
     */
    public Failure(Description description, Throwable thrownException) {
        this.fThrownException = thrownException;
        this.fDescription = description;
    }

    /**
     * @return a user-understandable label for the test
     */
    public String getTestHeader() {
        return fDescription.getDisplayName();
    }

    /**
     * @return the raw description of the context of the failure.
     */
    public Description getDescription() {
        return fDescription;
    }

    /**
     * @return the exception thrown
     */

    public Throwable getException() {
        return fThrownException;
    }

    @Override
    public String toString() {
        return getTestHeader() + ": " + fThrownException.getMessage();
    }

    /**
     * Gets the printed form of the exception and its stack trace.
     */
    public String getTrace() {
        return Throwables.getStacktrace(getException());
    }

    /**
     * Gets a the printed form of the exception, with a trimmed version of the stack trace.
     * This method will attempt to filter out frames of the stack trace that are below
     * the test method call.
     */
    public String getTrimmedTrace() {
        return Throwables.getTrimmedStackTrace(getException());
    }

    /**
     * Convenience method
     *
     * @return the message of the thrown exception
     */
    public String getMessage() {
        return getException().getMessage();
    }
}
