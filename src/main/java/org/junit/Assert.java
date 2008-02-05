package org.junit;

import java.lang.reflect.Array;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.internal.ArrayComparisonFailure;

/**
 * A set of assertion methods useful for writing tests. Only failed assertions are recorded.
 * These methods can be used directly: <code>Assert.assertEquals(...)</code>, however, they
 * read better if they are referenced through static import:<br/>
 * <pre>
 * import static org.junit.Assert.*;
 *    ...
 *    assertEquals(...);
 * </pre>
 *   
 * @see AssertionError
 */
public class Assert {
	/**
	 * Protect constructor since it is a static only class
	 */
	protected Assert() {
	}
	
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
		throw new AssertionError(message == null ? "" : message);
	}

	/**
	 * Fails a test with no message.
	 */
	static public void fail() {
		fail(null);
	}
	
	/**
	 * Asserts that two objects are equal. If they are not, an {@link AssertionError} 
	 * is thrown with the given message. If <code>expected</code> and <code>actual</code>
	 * are <code>null</code>, they are considered equal.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expected expected value
	 * @param actual actual value
	 */
	static public void assertEquals(String message, Object expected, Object actual) {
		if (expected == null && actual == null)
			return;
		if (expected != null && isEquals(expected, actual))
			return;
		else if (expected instanceof String && actual instanceof String) {
			String cleanMessage= message == null ? "" : message;
			throw new ComparisonFailure(cleanMessage, (String)expected, (String)actual);
		}
		else
			failNotEquals(message, expected, actual);
	}

	private static boolean isEquals(Object expected, Object actual) {
		return expected.equals(actual);
	}

	/**
	 * Asserts that two objects are equal. If they are not, an {@link AssertionError} 
	 * without a message is thrown. If <code>expected</code> and <code>actual</code>
	 * are <code>null</code>, they are considered equal.
	 * @param expected expected value
	 * @param actual the value to check against <code>expected</code>
	 */
	static public void assertEquals(Object expected, Object actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message. If <code>expecteds</code> and
	 *  <code>actuals</code> are <code>null</code>, they are considered equal.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds Object array or array of arrays (multi-dimensional array) with expected values.
	 * @param actuals Object array or array of arrays (multi-dimensional array) with actual values
	 */
	public static void assertArrayEquals(String message, Object[] expecteds,
			Object[] actuals) throws ArrayComparisonFailure {
		internalArrayEquals(message, expecteds, actuals);
	}
	
	/**
	 * Asserts that two object arrays are equal. If they are not, an {@link AssertionError} 
	 * is thrown.  If <code>expected</code> and <code>actual</code> are <code>null</code>, 
	 * they are considered equal.
	 * @param expecteds Object array or array of arrays (multi-dimensional array) with expected values
	 * @param actuals Object array or array of arrays (multi-dimensional array) with actual values
	 */
	public static void assertArrayEquals(Object[] expecteds, Object[] actuals) {
		assertArrayEquals(null, expecteds, actuals);
	}

	/**
	 * Asserts that two byte arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds byte array with expected values.
	 * @param actuals byte array with actual values
	 */
	public static void assertArrayEquals(String message, byte[] expecteds,
			byte[] actuals) throws ArrayComparisonFailure {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two byte arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 * @param expecteds byte array with expected values.
	 * @param actuals byte array with actual values
	 */
	public static void assertArrayEquals(byte[] expecteds, byte[] actuals) {
		assertArrayEquals(null, expecteds, actuals);
	}
	
	/**
	 * Asserts that two char arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds char array with expected values.
	 * @param actuals char array with actual values
	 */
	public static void assertArrayEquals(String message, char[] expecteds,
			char[] actuals) throws ArrayComparisonFailure {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two char arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 * @param expecteds char array with expected values.
	 * @param actuals char array with actual values
	 */
	public static void assertArrayEquals(char[] expecteds, char[] actuals) {
		assertArrayEquals(null, expecteds, actuals);
	}
	
	/**
	 * Asserts that two short arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds short array with expected values.
	 * @param actuals short array with actual values
	 */
	public static void assertArrayEquals(String message, short[] expecteds,
			short[] actuals) throws ArrayComparisonFailure {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two short arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 * @param expecteds short array with expected values.
	 * @param actuals short array with actual values
	 */
	public static void assertArrayEquals(short[] expecteds, short[] actuals) {
		assertArrayEquals(null, expecteds, actuals);
	}
	
	/**
	 * Asserts that two int arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds int array with expected values.
	 * @param actuals int array with actual values
	 */
	public static void assertArrayEquals(String message, int[] expecteds,
			int[] actuals) throws ArrayComparisonFailure {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two int arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 * @param expecteds int array with expected values.
	 * @param actuals int array with actual values
	 */
	public static void assertArrayEquals(int[] expecteds, int[] actuals) {
		assertArrayEquals(null, expecteds, actuals);
	}

	/**
	 * Asserts that two long arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds long array with expected values.
	 * @param actuals long array with actual values
	 */
	public static void assertArrayEquals(String message, long[] expecteds,
			long[] actuals) throws ArrayComparisonFailure {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two long arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 * @param expecteds long array with expected values.
	 * @param actuals long array with actual values
	 */
	public static void assertArrayEquals(long[] expecteds, long[] actuals) {
		assertArrayEquals(null, expecteds, actuals);
	}
	
	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message. If <code>expecteds</code> and
	 *  <code>actuals</code> are <code>null</code>, they are considered equal.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds Object array or array of arrays (multi-dimensional array) with expected values.
	 * @param actuals Object array or array of arrays (multi-dimensional array) with actual values
	 */
	private static void internalArrayEquals(String message, Object expecteds,
			Object actuals) throws ArrayComparisonFailure {
		if (expecteds == actuals)
			return;
		String header = message == null ? "" : message + ": ";
		if (expecteds == null)
			fail(header + "expected array was null");
		if (actuals == null)
			fail(header + "actual array was null");
		int actualsLength= Array.getLength(actuals);
		int expectedsLength= Array.getLength(expecteds);
		if (actualsLength != expectedsLength)
			fail(header + "array lengths differed, expected.length=" + expectedsLength + " actual.length=" + actualsLength);
	
		for (int i= 0; i < expectedsLength; i++) {
			Object expected= Array.get(expecteds, i);
			Object actual= Array.get(actuals, i);
			if (isArray(expected) && isArray(actual)) {
				try {
					internalArrayEquals(message, expected, actual);
				} catch (ArrayComparisonFailure e) {
					e.addDimension(i);
					throw e;
				}
			} else
				try {
					assertEquals(expected, actual);
				} catch (AssertionError e) {
					throw new ArrayComparisonFailure(header, e, i);
				}
		}
	}

	private static boolean isArray(Object expected) {
		return expected != null && expected.getClass().isArray();
	}

	/**
	 * Asserts that two doubles or floats are equal to within a positive delta. If they
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
	
	static public void assertEquals(long expected, long actual) {
		assertEquals(null, expected, actual);
	}
	
	static public void assertEquals(String message, long expected, long actual) {
		assertEquals(message, (Long)expected, (Long)actual);
	}

	static public void assertEquals(double expected, double actual) {
		assertEquals(null, expected, actual);
	}

	static public void assertEquals(String message, double expected, double actual) {
		fail("Use assertEquals(expected, actual, delta) to compare floating-point numbers");
	}
	
	/**
	 * Asserts that two doubles or floats are equal to within a positive delta. If they
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
		if (message != null && ! message.equals(""))
			formatted= message + " ";
		String expectedString= String.valueOf(expected);
		String actualString= String.valueOf(actual);
		if (expectedString.equals(actualString))
			return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: " + formatClassAndValue(actual, actualString);
		else
			return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
	}
	
	private static String formatClassAndValue(Object value, String valueString) {
		String className= value == null ? "null" : value.getClass().getName();
		return className + "<" + valueString + ">";
	}

	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message. If <code>expecteds</code> and
	 *  <code>actuals</code> are <code>null</code>, they are considered equal.
	 * @param message the identifying message or <code>null</code> for the {@link AssertionError}
	 * @param expecteds Object array or array of arrays (multi-dimensional array) with expected values.
	 * @param actuals Object array or array of arrays (multi-dimensional array) with actual values
	 * @deprecated use assertArrayEquals
	 */
	@Deprecated
	public static void assertEquals(String message, Object[] expecteds, Object[] actuals) {
		assertArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two object arrays are equal. If they are not, an {@link AssertionError} 
	 * is thrown.  If <code>expected</code> and <code>actual</code> are <code>null</code>, 
	 * they are considered equal.
	 * @param expecteds Object array or array of arrays (multi-dimensional array) with expected values
	 * @param actuals Object array or array of arrays (multi-dimensional array) with actual values
	 * @deprecated use assertArrayEquals
	 */
	@Deprecated
	public static void assertEquals(Object[] expecteds, Object[] actuals) {
		assertArrayEquals(expecteds, actuals);
	}


    public static <T> void assertThat(T actual, Matcher<T> matcher) {
        assertThat("", actual, matcher);
    }
    
    public static <T> void assertThat(String reason, T actual, Matcher<T> matcher) {
        if (!matcher.matches(actual)) {
            Description description = new StringDescription();
            description.appendText(reason);
            description.appendText("\nExpected: ");
            matcher.describeTo(description);
            description.appendText("\n     got: ").appendValue(actual).appendText("\n");
            throw new java.lang.AssertionError(description.toString());
        }
    }
}
