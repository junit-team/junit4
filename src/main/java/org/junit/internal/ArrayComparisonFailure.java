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

    /*
     * We have to use the f prefix until the next major release to ensure
     * serialization compatibility. 
     * See https://github.com/junit-team/junit/issues/976
     */
    private final List<Integer> fIndices = new ArrayList<Integer>();
    private final String fMessage;

    /**
     * Construct a new <code>ArrayComparisonFailure</code> with an error text and the array's
     * dimension that was not equal
     *
     * @param cause the exception that caused the array's content to fail the assertion test
     * @param index the array position of the objects that are not equal.
     * @see Assert#assertArrayEquals(String, Object[], Object[])
     */
    public ArrayComparisonFailure(String message, AssertionError cause, int index) {
        this.fMessage = message;
        initCause(cause);
        addDimension(index);
    }

    public void addDimension(int index) {
        fIndices.add(0, index);
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (fMessage != null) {
            sb.append(fMessage);
        }
        sb.append("arrays first differed at element ");
        for (int each : fIndices) {
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
