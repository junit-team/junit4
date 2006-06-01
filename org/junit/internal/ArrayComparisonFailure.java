package org.junit.internal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

/**
 * Thrown when two array elements differ
 * @see Assert#assertEquals(String, Object[], Object[])
 */
public class ArrayComparisonFailure extends AssertionError {

	private static final long serialVersionUID= 1L;
	
	private List<Integer> fIndices= new ArrayList<Integer>();
	private final String fMessage;
	private final AssertionError fCause;

	public ArrayComparisonFailure(String message, AssertionError cause, int index) {
		fMessage= message;
		fCause= cause;
		addDimension(index);
	}

	public void addDimension(int index) {
		fIndices.add(0, index);
	}

	@Override
	public String getMessage() {
		StringBuilder builder= new StringBuilder();
		if (fMessage != null)
			builder.append(fMessage);
		builder.append("arrays first differed at element ");
		for (int each : fIndices) {
			builder.append("[");
			builder.append(each);
			builder.append("]");
		}
		builder.append("; ");
		builder.append(fCause.getMessage());
		return builder.toString();
	}
}
