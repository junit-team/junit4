package org.junit.internal;

/**
 * An exception used to indicate that <i>assumption</i> is not supported in given context.
 *
 * @see org.junit.Assume
 */
public class AssumptionNotSupportedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AssumptionNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
