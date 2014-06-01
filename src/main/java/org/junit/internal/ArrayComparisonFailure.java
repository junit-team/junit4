package org.junit.internal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

/**
 * Thrown when two array elements differ
 *
 * @see Assert#assertArrayEquals(String, Object[], Object[])
 */
public class ArrayComparisonFailure extends AssertionError {

    private static final long serialVersionUID = 1L;

    private final List<Integer> indices = new ArrayList<Integer>();
    private final String message;

    /**
     * Construct a new <code>ArrayComparisonFailure</code> with an error text and the array's
     * dimension that was not equal
     *
     * @param cause the exception that caused the array's content to fail the assertion test
     * @param index the array position of the objects that are not equal.
     * @see Assert#assertArrayEquals(String, Object[], Object[])
     */
    public ArrayComparisonFailure(String message, AssertionError cause, int index) {
        this.message = message;
        initCause(cause);
        addDimension(index);
    }

    public void addDimension(int index) {
        indices.add(0, index);
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (message != null) {
            sb.append(message);
        }
        sb.append("arrays first differed at element ");
        for (int each : indices) {
            sb.append("[");
            sb.append(each);
            sb.append("]");
        }
        sb.append("; ");
        sb.append(getCause().getMessage());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getMessage();
    }
}
