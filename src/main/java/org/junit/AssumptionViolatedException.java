package org.junit;

import org.hamcrest.Matcher;

/**
 * An exception class used to implement <i>assumptions</i> (state in which a given test
 * is meaningful and should or should not be executed). A test for which an assumption
 * fails should not generate a test case failure.
 *
 * @see org.junit.Assume
 * @since 4.12
 */
@SuppressWarnings("deprecation")
public class AssumptionViolatedException extends org.junit.internal.AssumptionViolatedException {
    private static final long serialVersionUID = 1L;

    /**
     * An assumption exception with the given <i>actual</i> value and a <i>matcher</i> describing 
     * the expectation that failed.
     */
    @Deprecated
    public <T> AssumptionViolatedException(T actual, Matcher<T> matcher) {
        super(actual, matcher);
    }

    /**
     * An assumption exception with a message with the given <i>actual</i> value and a
     * <i>matcher</i> describing the expectation that failed.
     */
    @Deprecated
    public <T> AssumptionViolatedException(String message, T expected, Matcher<T> matcher) {
        super(message, expected, matcher);
    }

    /**
     * An assumption exception with the given message only.
     */
    public AssumptionViolatedException(String message) {
        super(message);
    }

    /**
     * An assumption exception with the given message and a cause.
     */
    public AssumptionViolatedException(String assumption, Throwable t) {
        super(assumption, t);
    }
}
