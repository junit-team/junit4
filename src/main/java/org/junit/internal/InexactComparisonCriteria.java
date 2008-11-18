/**
 * 
 */
package org.junit.internal;

import java.lang.reflect.Array;

import org.junit.Assert;

public class InexactComparisonCriteria {
	public double fDelta;

	public InexactComparisonCriteria(double delta) {
		fDelta= delta;
	}

	// TODO (Sep 8, 2008 4:36:12 PM): check structure
	
	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message. If
	 * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
	 * they are considered equal.
	 * 
	 * @param message
	 *            the identifying message for the {@link AssertionError} (<code>null</code>
	 *            okay)
	 * @param expecteds
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            expected values.
	 * @param actuals
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            actual values
	 * @param criteria TODO
	 */
	public void internalArrayEquals(String message, Object expecteds,
			Object actuals) throws ArrayComparisonFailure {
		// TODO: DUP above
		// TODO (Sep 8, 2008 4:32:50 PM): Test that this fails sometimes
		// TODO (Sep 8, 2008 4:33:04 PM): Update javadoc
		
		if (expecteds == actuals)
			return;
		String header= message == null ? "" : message + ": ";
		
		int expectedsLength= Assert.assertArraysAreSameLength(expecteds, actuals,
				header);

		for (int i= 0; i < expectedsLength; i++) {
			Object expected= Array.get(expecteds, i);
			Object actual= Array.get(actuals, i);
			
			if (Assert.isArray(expected) && Assert.isArray(actual)) {
				try {
					internalArrayEquals(message, expected, actual);
				} catch (ArrayComparisonFailure e) {
					e.addDimension(i);
					throw e;
				}
			} else
				try {
					Assert.assertEquals((Double)expected, (Double)actual, fDelta);
				} catch (AssertionError e) {
					throw new ArrayComparisonFailure(header, e, i);
				}
		}
	}
}