package org.junit;

/**
 * An exception class used to implement <i>assumptions</i> (state in which a given test
 * is meaningful and should or should not be executed). A test for which an assumption
 * fails should not generate a test case failure.
 *
 * @see org.junit.Assume
 * @since 4.12
 */
public class AssumptionViolatedException extends org.junit.internal.AssumptionViolatedException {

    private static final long serialVersionUID = 2L;

    /**
     * An assumption exception with the given message only.
     */
    @SuppressWarnings("deprecation")
    public AssumptionViolatedException(String message) {
        super(message);
    }

    /**
     * An assumption exception with the given message and a cause.
     */
    @SuppressWarnings("deprecation")
    public AssumptionViolatedException(String message, Throwable e) {
        super(message, e);
    }

}
