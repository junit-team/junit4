package org.junit;

/**
 * Indicates that a test that indicated that it should be skipped could not be skipped.
 * This can be thrown if a test uses the methods in {@link Assume} to indicate that
 * it should be skipped, but before processing of the test was completed, other failures
 * occured.
 *
 * @see org.junit.Assume
 * @since 4.13
 */
public class TestCouldNotBeSkippedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** Creates an instance using the given assumption failure. */
    public TestCouldNotBeSkippedException(org.junit.internal.AssumptionViolatedException cause) {
        super("Test could not be skipped due to other failures", cause);
    }
}
