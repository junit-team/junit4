package org.junit;

/**
 * A set of assertion methods useful for writing tests. Only failed assertions are recorded.
 * These methods can be used directly: <code>Assert.assertEquals(...)</code>, however, they
 * read better if they are referenced through static import:<br>
 * <code>
 *   import static org.junit.Assert.*;<br>
 *   ...<br>
 *   &nbsp;&nbsp;assertEquals(...);<br>
 *   </code>
 *   
 *   @see java.lang.AssertionError
 */
public class Assert {
	/**
	 * Protect constructor since it is a static only class
	 */
	protected Assert() {
	}
	
	// TODO: param comments should start with lower case letters

	/**
	 * Asserts that a condition is true. If it isn't it throws an
	 * {@link AssertionError} with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param condition condition to be checked
	 */
	static public void assertTrue(String message, boolean condition) {
		if (!condition)
			fail(message);
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws an
	 * {@link AssertionError} without a message.
	 * @param condition condition to be checked
	 */
	static public void assertTrue(boolean condition) {
		assertTrue(null, condition);
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws an
	 * {@link AssertionError} with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param condition condition to be checked
	 */
	static public void assertFalse(String message, boolean condition) {
		assertTrue(message, !condition);
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws an
	 * {@link AssertionError} without a message.
	 * @param condition condition to be checked
	 */
	static public void assertFalse(boolean condition) {
		assertFalse(null, condition);
	}

	/**
	 * Fails a test with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @see AssertionError
	 */
	static public void fail(String message) {
		throw new AssertionError(message);
	}

	/**
	 * Fails a test with no message.
	 */
	static public void fail() {
		fail(null);
	}
	
	/**
	 * Asserts that two objects are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expected expected value
	 * @param actual actual value
	 */
	static public void assertEquals(String message, Object expected, Object actual) {
		if (expected == null && actual == null)
			return;
		if (expected != null && expected.equals(actual))
			return;
		if (expected instanceof String && actual instanceof String)
			throw new ComparisonFailure(message, (String)expected, (String)actual);
		else
			failNotEquals(message, expected, actual);
	}

	/**
	 * Asserts that two objects are equal. If they are not, an
	 * {@link AssertionError} without a message is thrown.
	 * @param expected expected value
	 * @param actual the value to check against <code>expected</code>
	 */
	static public void assertEquals(Object expected, Object actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds Object array or array of arrays (multi-dimensional array) with expected values
	 * @param actuals Object array or array of arrays (multi-dimensional array) with actual values
	 */
	public static void assertEquals(String message, Object[] expecteds, Object[] actuals) {
		if (expecteds == actuals)
			return;
		String header = message == null ? "" : message + ": ";
		if (expecteds == null)
			fail(header + "expected array was null");
		if (actuals == null)
			fail(header + "actual array was null");
		if (actuals.length != expecteds.length)
			fail(header + "array lengths differed, expected.length=" + expecteds.length + " actual.length=" + actuals.length);

		for (int i= 0; i < expecteds.length; i++) {
			Object o1= expecteds[i];
			Object o2= actuals[i];
			if (o1.getClass().isArray() && o2.getClass().isArray()) {
				Object[] expected= (Object[]) o1;
				Object[] actual= (Object[]) o2;
				assertEquals(header + "arrays first differed at element " + i + ";", expected, actual);
			} else
				assertEquals(header + "arrays first differed at element [" + i + "];", o1, o2);
		}
	}

	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 * @param expecteds Object array or array of arrays (multi-dimensional array) with expected values
	 * @param actuals Object array or array of arrays (multi-dimensional array) with actual values
	 */
	public static void assertEquals(Object[] expecteds, Object[] actuals) {
		assertEquals(null, expecteds, actuals);
	}

	/**
	 * Asserts that two doubles are equal to within a positive delta. If they
	 * are not, an {@link AssertionError} is thrown with the given message. If the
	 * expected value is infinity then the delta value is ignored. NaNs are
	 * considered equal:
	 * <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expected expected value
	 * @param actual the value to check against <code>expected</code>
	 * @param delta the maximum delta between <code>expected</code> and <code>actual</code> for which 
	 * both numbers are still considered equal.
	 */
	static public void assertEquals(String message, double expected, double actual, double delta) {
		if (Double.compare(expected, actual) == 0)
			return;
		if (!(Math.abs(expected - actual) <= delta))
			failNotEquals(message, new Double(expected), new Double(actual));
	}

	/**
	 * Asserts that two doubles are equal to within a positive delta. If they
	 * are not, an {@link AssertionError} is thrown. If the
	 * expected value is infinity then the delta value is ignored.NaNs are
	 * considered equal:
	 * <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
	 * @param expected expected value
	 * @param actual the value to check against <code>expected</code>
	 * @param delta the maximum delta between <code>expected</code> and <code>actual</code> for which 
	 * both numbers are still considered equal.
	 */
	static public void assertEquals(double expected, double actual, double delta) {
		assertEquals(null, expected, actual, delta);
	}

	/**
	 * Asserts that two floats are equal to within a positive delta. If they
	 * are not, an {@link AssertionError} is thrown with the given message. If the
	 * expected value is infinity then the delta value is ignored.NaNs are
	 * considered equal:
	 * <code>assertEquals(Float.NaN, Float.NaN, *)</code> passes
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expected the expected float value
	 * @param actual the float value to check against <code>expected</code>
	 * @param delta the maximum delta between <code>expected</code> and <code>actual</code> for which 
	 * both numbers are still considered equal.
	 */
	static public void assertEquals(String message, float expected, float actual, float delta) {
		if (Float.compare(expected, actual) == 0)
			return;
		if (!(Math.abs(expected - actual) <= delta))
			failNotEquals(message, new Float(expected), new Float(actual));
	}

	/**
	 * Asserts that two floats are equal to within a positive delta. If they
	 * are not, an {@link AssertionError} is thrown. If the
	 * expected value is infinity then the delta value is ignored. {@link Float#NaN NaNs} are
	 * considered equal:
	 * <code>assertEquals(Float.NaN, Float.NaN, *)</code> passes
	 * @param expected the expected value
	 * @param actual the value to check against <code>expected</code>
	 * @param delta the maximum delta between <code>expected</code> and <code>actual</code> for which 
	 * both numbers are still considered equal.
	 */
	static public void assertEquals(float expected, float actual, float delta) {
		assertEquals(null, expected, actual, delta);
	}

	/**
	 * Asserts that an object isn't null. If it is an {@link AssertionError} is
	 * thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param object Object to check or <code>null</code>
	 */
	static public void assertNotNull(String message, Object object) {
		assertTrue(message, object != null);
	}

	/**
	 * Asserts that an object isn't null. If it is an {@link AssertionError} is
	 * thrown.
	 * @param object Object to check or <code>null</code>
	 */
	static public void assertNotNull(Object object) {
		assertNotNull(null, object);
	}
	
	/**
	 * Asserts that an object is null. If it is not, an {@link AssertionError} is
	 * thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param object Object to check or <code>null</code>
	 */
	static public void assertNull(String message, Object object) {
		assertTrue(message, object == null);
	}

	/**
	 * Asserts that an object is null. If it isn't an {@link AssertionError} is
	 * thrown.
	 * @param object Object to check or <code>null</code>
	 */
	static public void assertNull(Object object) {
		assertNull(null, object);
	}
	
	/**
	 * Asserts that two objects refer to the same object. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expected the expected object
	 * @param actual the object to compare to <code>expected</code>
	 */
	static public void assertSame(String message, Object expected, Object actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not the
	 * same, an {@link AssertionError} without a message is thrown.
	 * @param expected the expected object
	 * @param actual the object to compare to <code>expected</code>
	 */
	static public void assertSame(Object expected, Object actual) {
		assertSame(null, expected, actual);
	}

	/**
	 * Asserts that two objects do not refer to the same object. If they do
	 * refer to the same object, an {@link AssertionError} is thrown with the given
	 * message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param unexpected the object you don't expect
	 * @param actual the object to compare to <code>unexpected</code>
	 */
	static public void assertNotSame(String message, Object unexpected, Object actual) {
		if (unexpected == actual)
			failSame(message);
	}

	/**
	 * Asserts that two objects do not refer to the same object. If they do
	 * refer to the same object, an {@link AssertionError} without a message is thrown.
	 * @param unexpected the object you don't expect
	 * @param actual the object to compare to <code>unexpected</code>
	 */
	static public void assertNotSame(Object unexpected, Object actual) {
		assertNotSame(null, unexpected, actual);
	}

	static private void failSame(String message) {
		String formatted= "";
		if (message != null)
			formatted= message + " ";
		fail(formatted + "expected not same");
	}

	static private void failNotSame(String message, Object expected, Object actual) {
		String formatted= "";
		if (message != null)
			formatted= message + " ";
		fail(formatted + "expected same:<" + expected + "> was not:<" + actual + ">");
	}

	static private void failNotEquals(String message, Object expected, Object actual) {
		fail(format(message, expected, actual));
	}

	static String format(String message, Object expected, Object actual) {
		String formatted= "";
		if (message != null)
			formatted= message + " ";
		return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
	}

}
