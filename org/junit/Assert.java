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
 */

public class Assert {
	/**
	 * Protect constructor since it is a static only class
	 */
	protected Assert() {
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws an
	 * AssertionError with the given message.
	 */
	static public void assertTrue(String message, boolean condition) {
		if (!condition)
			fail(message);
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws an
	 * AssertionError.
	 */
	static public void assertTrue(boolean condition) {
		assertTrue(null, condition);
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws an
	 * AssertionError with the given message.
	 */
	static public void assertFalse(String message, boolean condition) {
		assertTrue(message, !condition);
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws an
	 * AssertionError.
	 */
	static public void assertFalse(boolean condition) {
		assertFalse(null, condition);
	}

	/**
	 * Fails a test with the given message.
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
	 * AssertionError is thrown with the given message.
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
	 * AssertionError is thrown.
	 */
	static public void assertEquals(Object expected, Object actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * AssertionError is thrown with the given message.
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
	 * AssertionError is thrown.
	 */
	public static void assertEquals(Object[] expecteds, Object[] actuals) {
		assertEquals(null, expecteds, actuals);
	}

	/**
	 * Asserts that two doubles are equal to within a positive delta. If they
	 * are not, an AssertionError is thrown with the given message. If the
	 * expected value is infinity then the delta value is ignored. NaNs are
	 * considered equal:
	 *   assertEquals(Double.NaN, Double.NaN, *) passes
	 */
	static public void assertEquals(String message, double expected, double actual, double delta) {
		if (Double.compare(expected, actual) == 0)
			return;
		if (!(Math.abs(expected - actual) <= delta))
			failNotEquals(message, new Double(expected), new Double(actual));
	}

	/**
	 * Asserts that two doubles are equal to within a positive delta. If they
	 * are not, an AssertionError is thrown. If the
	 * expected value is infinity then the delta value is ignored.NaNs are
	 * considered equal:
	 *   assertEquals(Double.NaN, Double.NaN, *) passes
	 */
	static public void assertEquals(double expected, double actual, double delta) {
		assertEquals(null, expected, actual, delta);
	}

	/**
	 * Asserts that two floats are equal to within a positive delta. If they
	 * are not, an AssertionError is thrown with the given message. If the
	 * expected value is infinity then the delta value is ignored.NaNs are
	 * considered equal:
	 *   assertEquals(Float.NaN, Float.NaN, *) passes
	 */
	static public void assertEquals(String message, float expected, float actual, float delta) {
		if (Float.compare(expected, actual) == 0)
			return;
		if (!(Math.abs(expected - actual) <= delta))
			failNotEquals(message, new Float(expected), new Float(actual));
	}

	/**
	 * Asserts that two floats are equal to within a positive delta. If they
	 * are not, an AssertionError is thrown. If the
	 * expected value is infinity then the delta value is ignored.NaNs are
	 * considered equal:
	 *   assertEquals(Float.NaN, Float.NaN, *) passes
	 */
	static public void assertEquals(float expected, float actual, float delta) {
		assertEquals(null, expected, actual, delta);
	}

	/**
	 * Asserts that an object isn't null. If it is an AssertionError is
	 * thrown with the given message.
	 */
	static public void assertNotNull(String message, Object object) {
		assertTrue(message, object != null);
	}

	/**
	 * Asserts that an object isn't null. If it is an AssertionError is
	 * thrown.
	 */
	static public void assertNotNull(Object object) {
		assertNotNull(null, object);
	}
	
	/**
	 * Asserts that an object is null. If it is not, an AssertionError is
	 * thrown with the given message.
	 */
	static public void assertNull(String message, Object object) {
		assertTrue(message, object == null);
	}

	/**
	 * Asserts that an object is null. If it isn't an AssertionError is
	 * thrown.
	 */
	static public void assertNull(Object object) {
		assertNull(null, object);
	}
	
	/**
	 * Asserts that two objects refer to the same object. If they are not, an
	 * AssertionError is thrown with the given message.
	 */
	static public void assertSame(String message, Object expected, Object actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not the
	 * same, an AssertionError is thrown.
	 */
	static public void assertSame(Object expected, Object actual) {
		assertSame(null, expected, actual);
	}

	/**
	 * Asserts that two objects do not refer to the same object. If they do
	 * refer to the same object, an AssertionError is thrown with the given
	 * message.
	 */
	static public void assertNotSame(String message, Object expected, Object actual) {
		if (expected == actual)
			failSame(message);
	}

	/**
	 * Asserts that two objects do not refer to the same object. If they do
	 * refer to the same object, an AssertionError is thrown.
	 */
	static public void assertNotSame(Object expected, Object actual) {
		assertNotSame(null, expected, actual);
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
