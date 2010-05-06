package org.junit.internal;

import java.lang.reflect.Array;

import org.junit.Assert;

/**
 * Defines criteria for finding two items "equal enough". Concrete subclasses
 * may demand exact equality, or, for example, equality within a given delta.
 */
public abstract class ComparisonCriteria {
	/**
	 * Asserts that two arrays are equal, according to the criteria defined by
	 * the concrete subclass. If they are not, an {@link AssertionError} is
	 * thrown with the given message. If <code>expecteds</code> and
	 * <code>actuals</code> are <code>null</code>, they are considered equal.
	 * 
	 * @param message
	 *            the identifying message for the {@link AssertionError} (
	 *            <code>null</code> okay)
	 * @param expecteds
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            expected values.
	 * @param actuals
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            actual values
	 */
	public void arrayEquals(String message, Object expecteds, Object actuals)
			throws ArrayComparisonFailure {
		if (expecteds == actuals)
			return;
		String header= message == null ? "" : message + ": ";

		int expectedsLength= assertArraysAreSameLength(expecteds,
				actuals, header);

		for (int i= 0; i < expectedsLength; i++) {
			Object expected= Array.get(expecteds, i);
			Object actual= Array.get(actuals, i);

			if (isArray(expected) && isArray(actual)) {
				try {
					arrayEquals(message, expected, actual);
				} catch (ArrayComparisonFailure e) {
					e.addDimension(i);
					throw e;
				}
			} else
				try {
					assertElementsEqual(expected, actual);
				} catch (AssertionError e) {
					throw new ArrayComparisonFailure(header, e, i);
				}
		}
	}

	private boolean isArray(Object expected) {
		return expected != null && expected.getClass().isArray();
	}

	private int assertArraysAreSameLength(Object expecteds,
			Object actuals, String header) {
		if (expecteds == null)
			Assert.fail(header + "expected array was null");
		if (actuals == null)
			Assert.fail(header + "actual array was null");
		int actualsLength= Array.getLength(actuals);
		int expectedsLength= Array.getLength(expecteds);
		if (actualsLength != expectedsLength)
			Assert.fail(header + "array lengths differed, expected.length="
					+ expectedsLength + " actual.length=" + actualsLength);
		return expectedsLength;
	}

	protected abstract void assertElementsEqual(Object expected, Object actual);
}
