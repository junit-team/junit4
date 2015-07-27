package org.junit;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.internal.ExactComparisonCriteria;
import org.junit.internal.InexactComparisonCriteria;

/**
 * A set of assertion methods useful for writing tests. Only failed assertions
 * are recorded. These methods can be used directly:
 * <code>Assert.assertEquals(...)</code>, however, they read better if they
 * are referenced through static import:
 *
 * <pre>
 * import static org.junit.Assert.*;
 *    ...
 *    assertEquals(...);
 * </pre>
 *
 * @see AssertionError
 * @since 4.0
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
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param condition condition to be checked
     */
    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * {@link AssertionError} without a message.
     *
     * @param condition condition to be checked
     */
    public static void assertTrue(boolean condition) {
        assertTrue(null, condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * {@link AssertionError} with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param condition condition to be checked
     */
    public static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * {@link AssertionError} without a message.
     *
     * @param condition condition to be checked
     */
    public static void assertFalse(boolean condition) {
        assertFalse(null, condition);
    }

    /**
     * Fails a test with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @see AssertionError
     */
    public static void fail(String message) {
        if (message == null) {
            throw new AssertionError();
        }
        throw new AssertionError(message);
    }

    /**
     * Fails a test with no message.
     */
    public static void fail() {
        fail(null);
    }

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expected expected value
     * @param actual actual value
     */
    public static void assertEquals(String message, Object expected,
            Object actual) {
        if (equalsRegardingNull(expected, actual)) {
            return;
        }
        if (expected instanceof String && actual instanceof String) {
            String cleanMessage = message == null ? "" : message;
            throw new ComparisonFailure(cleanMessage, (String) expected,
                    (String) actual);
        } else {
            failNotEquals(message, expected, actual);
        }
    }

    private static boolean equalsRegardingNull(Object expected, Object actual) {
        if (expected == null) {
            return actual == null;
        }

        return isEquals(expected, actual);
    }

    private static boolean isEquals(Object expected, Object actual) {
        return expected.equals(actual);
    }

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link AssertionError} without a message is thrown. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param expected expected value
     * @param actual the value to check against <code>expected</code>
     */
    public static void assertEquals(Object expected, Object actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two objects are <b>not</b> equals. If they are, an
     * {@link AssertionError} is thrown with the given message. If
     * <code>unexpected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param unexpected unexpected value to check
     * @param actual the value to check against <code>unexpected</code>
     */
    public static void assertNotEquals(String message, Object unexpected,
            Object actual) {
        if (equalsRegardingNull(unexpected, actual)) {
            failEquals(message, actual);
        }
    }

    /**
     * Asserts that two objects are <b>not</b> equals. If they are, an
     * {@link AssertionError} without a message is thrown. If
     * <code>unexpected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param unexpected unexpected value to check
     * @param actual the value to check against <code>unexpected</code>
     */
    public static void assertNotEquals(Object unexpected, Object actual) {
        assertNotEquals(null, unexpected, actual);
    }

    private static void failEquals(String message, Object actual) {
        String formatted = "Values should be different. ";
        if (message != null) {
            formatted = message + ". ";
        }

        formatted += "Actual: " + actual;
        fail(formatted);
    }

    /**
     * Asserts that two longs are <b>not</b> equals. If they are, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param unexpected unexpected value to check
     * @param actual the value to check against <code>unexpected</code>
     */
    public static void assertNotEquals(String message, long unexpected, long actual) {
        if (unexpected == actual) {
            failEquals(message, Long.valueOf(actual));
        }
    }

    /**
     * Asserts that two longs are <b>not</b> equals. If they are, an
     * {@link AssertionError} without a message is thrown.
     *
     * @param unexpected unexpected value to check
     * @param actual the value to check against <code>unexpected</code>
     */
    public static void assertNotEquals(long unexpected, long actual) {
        assertNotEquals(null, unexpected, actual);
    }

    /**
     * Asserts that two doubles are <b>not</b> equal to within a positive delta.
     * If they are, an {@link AssertionError} is thrown with the given
     * message. If the unexpected value is infinity then the delta value is
     * ignored. NaNs are considered equal:
     * <code>assertNotEquals(Double.NaN, Double.NaN, *)</code> fails
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param unexpected unexpected value
     * @param actual the value to check against <code>unexpected</code>
     * @param delta the maximum delta between <code>unexpected</code> and
     * <code>actual</code> for which both numbers are still
     * considered equal.
     */
    public static void assertNotEquals(String message, double unexpected,
            double actual, double delta) {
        if (!doubleIsDifferent(unexpected, actual, delta)) {
            failEquals(message, Double.valueOf(actual));
        }
    }

    /**
     * Asserts that two doubles are <b>not</b> equal to within a positive delta.
     * If they are, an {@link AssertionError} is thrown. If the unexpected
     * value is infinity then the delta value is ignored.NaNs are considered
     * equal: <code>assertNotEquals(Double.NaN, Double.NaN, *)</code> fails
     *
     * @param unexpected unexpected value
     * @param actual the value to check against <code>unexpected</code>
     * @param delta the maximum delta between <code>unexpected</code> and
     * <code>actual</code> for which both numbers are still
     * considered equal.
     */
    public static void assertNotEquals(double unexpected, double actual, double delta) {
        assertNotEquals(null, unexpected, actual, delta);
    }

    /**
     * Asserts that two floats are <b>not</b> equal to within a positive delta.
     * If they are, an {@link AssertionError} is thrown. If the unexpected
     * value is infinity then the delta value is ignored.NaNs are considered
     * equal: <code>assertNotEquals(Float.NaN, Float.NaN, *)</code> fails
     *
     * @param unexpected unexpected value
     * @param actual the value to check against <code>unexpected</code>
     * @param delta the maximum delta between <code>unexpected</code> and
     * <code>actual</code> for which both numbers are still
     * considered equal.
     */
    public static void assertNotEquals(float unexpected, float actual, float delta) {
        assertNotEquals(null, unexpected, actual, delta);
    }

    /**
     * Asserts that two object arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expecteds Object array or array of arrays (multi-dimensional array) with
     * expected values.
     * @param actuals Object array or array of arrays (multi-dimensional array) with
     * actual values
     */
    public static void assertArrayEquals(String message, Object[] expecteds,
            Object[] actuals) throws ArrayComparisonFailure {
        internalArrayEquals(message, expecteds, actuals);
    }

    /**
     * Asserts that two object arrays are equal. If they are not, an
     * {@link AssertionError} is thrown. If <code>expected</code> and
     * <code>actual</code> are <code>null</code>, they are considered
     * equal.
     *
     * @param expecteds Object array or array of arrays (multi-dimensional array) with
     * expected values
     * @param actuals Object array or array of arrays (multi-dimensional array) with
     * actual values
     */
    public static void assertArrayEquals(Object[] expecteds, Object[] actuals) {
        assertArrayEquals(null, expecteds, actuals);
    }

    /**
     * Asserts that two boolean arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expecteds boolean array with expected values.
     * @param actuals boolean array with expected values.
     */
    public static void assertArrayEquals(String message, boolean[] expecteds,
            boolean[] actuals) throws ArrayComparisonFailure {
        internalArrayEquals(message, expecteds, actuals);
    }

    /**
     * Asserts that two boolean arrays are equal. If they are not, an
     * {@link AssertionError} is thrown. If <code>expected</code> and
     * <code>actual</code> are <code>null</code>, they are considered
     * equal.
     *
     * @param expecteds boolean array with expected values.
     * @param actuals boolean array with expected values.
     */
    public static void assertArrayEquals(boolean[] expecteds, boolean[] actuals) {
        assertArrayEquals(null, expecteds, actuals);
    }

    /**
     * Asserts that two byte arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
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
     *
     * @param expecteds byte array with expected values.
     * @param actuals byte array with actual values
     */
    public static void assertArrayEquals(byte[] expecteds, byte[] actuals) {
        assertArrayEquals(null, expecteds, actuals);
    }

    /**
     * Asserts that two char arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
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
     *
     * @param expecteds char array with expected values.
     * @param actuals char array with actual values
     */
    public static void assertArrayEquals(char[] expecteds, char[] actuals) {
        assertArrayEquals(null, expecteds, actuals);
    }

    /**
     * Asserts that two short arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
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
     *
     * @param expecteds short array with expected values.
     * @param actuals short array with actual values
     */
    public static void assertArrayEquals(short[] expecteds, short[] actuals) {
        assertArrayEquals(null, expecteds, actuals);
    }

    /**
     * Asserts that two int arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
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
     *
     * @param expecteds int array with expected values.
     * @param actuals int array with actual values
     */
    public static void assertArrayEquals(int[] expecteds, int[] actuals) {
        assertArrayEquals(null, expecteds, actuals);
    }

    /**
     * Asserts that two long arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
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
     *
     * @param expecteds long array with expected values.
     * @param actuals long array with actual values
     */
    public static void assertArrayEquals(long[] expecteds, long[] actuals) {
        assertArrayEquals(null, expecteds, actuals);
    }

    /**
     * Asserts that two double arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expecteds double array with expected values.
     * @param actuals double array with actual values
     * @param delta the maximum delta between <code>expecteds[i]</code> and
     * <code>actuals[i]</code> for which both numbers are still
     * considered equal.
     */
    public static void assertArrayEquals(String message, double[] expecteds,
            double[] actuals, double delta) throws ArrayComparisonFailure {
        new InexactComparisonCriteria(delta).arrayEquals(message, expecteds, actuals);
    }

    /**
     * Asserts that two double arrays are equal. If they are not, an
     * {@link AssertionError} is thrown.
     *
     * @param expecteds double array with expected values.
     * @param actuals double array with actual values
     * @param delta the maximum delta between <code>expecteds[i]</code> and
     * <code>actuals[i]</code> for which both numbers are still
     * considered equal.
     */
    public static void assertArrayEquals(double[] expecteds, double[] actuals, double delta) {
        assertArrayEquals(null, expecteds, actuals, delta);
    }

    /**
     * Asserts that two float arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expecteds float array with expected values.
     * @param actuals float array with actual values
     * @param delta the maximum delta between <code>expecteds[i]</code> and
     * <code>actuals[i]</code> for which both numbers are still
     * considered equal.
     */
    public static void assertArrayEquals(String message, float[] expecteds,
            float[] actuals, float delta) throws ArrayComparisonFailure {
        new InexactComparisonCriteria(delta).arrayEquals(message, expecteds, actuals);
    }

    /**
     * Asserts that two float arrays are equal. If they are not, an
     * {@link AssertionError} is thrown.
     *
     * @param expecteds float array with expected values.
     * @param actuals float array with actual values
     * @param delta the maximum delta between <code>expecteds[i]</code> and
     * <code>actuals[i]</code> for which both numbers are still
     * considered equal.
     */
    public static void assertArrayEquals(float[] expecteds, float[] actuals, float delta) {
        assertArrayEquals(null, expecteds, actuals, delta);
    }

    /**
     * Asserts that two object arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expecteds Object array or array of arrays (multi-dimensional array) with
     * expected values.
     * @param actuals Object array or array of arrays (multi-dimensional array) with
     * actual values
     */
    private static void internalArrayEquals(String message, Object expecteds,
            Object actuals) throws ArrayComparisonFailure {
        new ExactComparisonCriteria().arrayEquals(message, expecteds, actuals);
    }

    /**
     * Asserts that two doubles are equal to within a positive delta.
     * If they are not, an {@link AssertionError} is thrown with the given
     * message. If the expected value is infinity then the delta value is
     * ignored. NaNs are considered equal:
     * <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expected expected value
     * @param actual the value to check against <code>expected</code>
     * @param delta the maximum delta between <code>expected</code> and
     * <code>actual</code> for which both numbers are still
     * considered equal.
     */
    public static void assertEquals(String message, double expected,
            double actual, double delta) {
        if (doubleIsDifferent(expected, actual, delta)) {
            failNotEquals(message, Double.valueOf(expected), Double.valueOf(actual));
        }
    }

    /**
     * Asserts that two floats are equal to within a positive delta.
     * If they are not, an {@link AssertionError} is thrown with the given
     * message. If the expected value is infinity then the delta value is
     * ignored. NaNs are considered equal:
     * <code>assertEquals(Float.NaN, Float.NaN, *)</code> passes
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expected expected value
     * @param actual the value to check against <code>expected</code>
     * @param delta the maximum delta between <code>expected</code> and
     * <code>actual</code> for which both numbers are still
     * considered equal.
     */
    public static void assertEquals(String message, float expected,
            float actual, float delta) {
        if (floatIsDifferent(expected, actual, delta)) {
            failNotEquals(message, Float.valueOf(expected), Float.valueOf(actual));
        }
    }

    /**
     * Asserts that two floats are <b>not</b> equal to within a positive delta.
     * If they are, an {@link AssertionError} is thrown with the given
     * message. If the unexpected value is infinity then the delta value is
     * ignored. NaNs are considered equal:
     * <code>assertNotEquals(Float.NaN, Float.NaN, *)</code> fails
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param unexpected unexpected value
     * @param actual the value to check against <code>unexpected</code>
     * @param delta the maximum delta between <code>unexpected</code> and
     * <code>actual</code> for which both numbers are still
     * considered equal.
     */
    public static void assertNotEquals(String message, float unexpected,
            float actual, float delta) {
        if (!floatIsDifferent(unexpected, actual, delta)) {
            failEquals(message, actual);
        }
    }

    private static boolean doubleIsDifferent(double d1, double d2, double delta) {
        if (Double.compare(d1, d2) == 0) {
            return false;
        }
        if ((Math.abs(d1 - d2) <= delta)) {
            return false;
        }

        return true;
    }

    private static boolean floatIsDifferent(float f1, float f2, float delta) {
        if (Float.compare(f1, f2) == 0) {
            return false;
        }
        if ((Math.abs(f1 - f2) <= delta)) {
            return false;
        }

        return true;
    }

    /**
     * Asserts that two longs are equal. If they are not, an
     * {@link AssertionError} is thrown.
     *
     * @param expected expected long value.
     * @param actual actual long value
     */
    public static void assertEquals(long expected, long actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two longs are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expected long expected value.
     * @param actual long actual value
     */
    public static void assertEquals(String message, long expected, long actual) {
        if (expected != actual) {
            failNotEquals(message, Long.valueOf(expected), Long.valueOf(actual));
        }
    }

    /**
     * @deprecated Use
     *             <code>assertEquals(double expected, double actual, double delta)</code>
     *             instead
     */
    @Deprecated
    public static void assertEquals(double expected, double actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * @deprecated Use
     *             <code>assertEquals(String message, double expected, double actual, double delta)</code>
     *             instead
     */
    @Deprecated
    public static void assertEquals(String message, double expected,
            double actual) {
        fail("Use assertEquals(expected, actual, delta) to compare floating-point numbers");
    }

    /**
     * Asserts that two doubles are equal to within a positive delta.
     * If they are not, an {@link AssertionError} is thrown. If the expected
     * value is infinity then the delta value is ignored.NaNs are considered
     * equal: <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
     *
     * @param expected expected value
     * @param actual the value to check against <code>expected</code>
     * @param delta the maximum delta between <code>expected</code> and
     * <code>actual</code> for which both numbers are still
     * considered equal.
     */
    public static void assertEquals(double expected, double actual, double delta) {
        assertEquals(null, expected, actual, delta);
    }

    /**
     * Asserts that two floats are equal to within a positive delta.
     * If they are not, an {@link AssertionError} is thrown. If the expected
     * value is infinity then the delta value is ignored. NaNs are considered
     * equal: <code>assertEquals(Float.NaN, Float.NaN, *)</code> passes
     *
     * @param expected expected value
     * @param actual the value to check against <code>expected</code>
     * @param delta the maximum delta between <code>expected</code> and
     * <code>actual</code> for which both numbers are still
     * considered equal.
     */
    public static void assertEquals(float expected, float actual, float delta) {
        assertEquals(null, expected, actual, delta);
    }

    /**
     * Asserts that an object isn't null. If it is an {@link AssertionError} is
     * thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param object Object to check or <code>null</code>
     */
    public static void assertNotNull(String message, Object object) {
        assertTrue(message, object != null);
    }

    /**
     * Asserts that an object isn't null. If it is an {@link AssertionError} is
     * thrown.
     *
     * @param object Object to check or <code>null</code>
     */
    public static void assertNotNull(Object object) {
        assertNotNull(null, object);
    }

    /**
     * Asserts that an object is null. If it is not, an {@link AssertionError}
     * is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param object Object to check or <code>null</code>
     */
    public static void assertNull(String message, Object object) {
        if (object == null) {
            return;
        }
        failNotNull(message, object);
    }

    /**
     * Asserts that an object is null. If it isn't an {@link AssertionError} is
     * thrown.
     *
     * @param object Object to check or <code>null</code>
     */
    public static void assertNull(Object object) {
        assertNull(null, object);
    }

    private static void failNotNull(String message, Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected null, but was:<" + actual + ">");
    }

    /**
     * Asserts that two objects refer to the same object. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expected the expected object
     * @param actual the object to compare to <code>expected</code>
     */
    public static void assertSame(String message, Object expected, Object actual) {
        if (expected == actual) {
            return;
        }
        failNotSame(message, expected, actual);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not the
     * same, an {@link AssertionError} without a message is thrown.
     *
     * @param expected the expected object
     * @param actual the object to compare to <code>expected</code>
     */
    public static void assertSame(Object expected, Object actual) {
        assertSame(null, expected, actual);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object, an {@link AssertionError} is thrown with the
     * given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param unexpected the object you don't expect
     * @param actual the object to compare to <code>unexpected</code>
     */
    public static void assertNotSame(String message, Object unexpected,
            Object actual) {
        if (unexpected == actual) {
            failSame(message);
        }
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object, an {@link AssertionError} without a message is
     * thrown.
     *
     * @param unexpected the object you don't expect
     * @param actual the object to compare to <code>unexpected</code>
     */
    public static void assertNotSame(Object unexpected, Object actual) {
        assertNotSame(null, unexpected, actual);
    }

    private static void failSame(String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected not same");
    }

    private static void failNotSame(String message, Object expected,
            Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected same:<" + expected + "> was not:<" + actual
                + ">");
    }

    private static void failNotEquals(String message, Object expected,
            Object actual) {
        fail(format(message, expected, actual));
    }

    static String format(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null && !"".equals(message)) {
            formatted = message + " ";
        }
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString)) {
            return formatted + "expected: "
                    + formatClassAndValue(expected, expectedString)
                    + " but was: " + formatClassAndValue(actual, actualString);
        } else {
            return formatted + "expected:<" + expectedString + "> but was:<"
                    + actualString + ">";
        }
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    /**
     * Asserts that two object arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expecteds Object array or array of arrays (multi-dimensional array) with
     * expected values.
     * @param actuals Object array or array of arrays (multi-dimensional array) with
     * actual values
     * @deprecated use assertArrayEquals
     */
    @Deprecated
    public static void assertEquals(String message, Object[] expecteds,
            Object[] actuals) {
        assertArrayEquals(message, expecteds, actuals);
    }

    /**
     * Asserts that two object arrays are equal. If they are not, an
     * {@link AssertionError} is thrown. If <code>expected</code> and
     * <code>actual</code> are <code>null</code>, they are considered
     * equal.
     *
     * @param expecteds Object array or array of arrays (multi-dimensional array) with
     * expected values
     * @param actuals Object array or array of arrays (multi-dimensional array) with
     * actual values
     * @deprecated use assertArrayEquals
     */
    @Deprecated
    public static void assertEquals(Object[] expecteds, Object[] actuals) {
        assertArrayEquals(expecteds, actuals);
    }

    /**
     * Asserts that <code>actual</code> satisfies the condition specified by
     * <code>matcher</code>. If not, an {@link AssertionError} is thrown with
     * information about the matcher and failing value. Example:
     *
     * <pre>
     *   assertThat(0, is(1)); // fails:
     *     // failure message:
     *     // expected: is &lt;1&gt;
     *     // got value: &lt;0&gt;
     *   assertThat(0, is(not(1))) // passes
     * </pre>
     *
     * <code>org.hamcrest.Matcher</code> does not currently document the meaning
     * of its type parameter <code>T</code>.  This method assumes that a matcher
     * typed as <code>Matcher&lt;T&gt;</code> can be meaningfully applied only
     * to values that could be assigned to a variable of type <code>T</code>.
     *
     * @param <T> the static type accepted by the matcher (this can flag obvious
     * compile-time problems such as {@code assertThat(1, is("a"))}
     * @param actual the computed value being compared
     * @param matcher an expression, built of {@link Matcher}s, specifying allowed
     * values
     * @see org.hamcrest.CoreMatchers
     * @see org.hamcrest.MatcherAssert
     * @deprecated use {@code org.hamcrest.junit.MatcherAssert.assertThat()}
     */
    @Deprecated
    public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
        assertThat("", actual, matcher);
    }

    /**
     * Asserts that <code>actual</code> satisfies the condition specified by
     * <code>matcher</code>. If not, an {@link AssertionError} is thrown with
     * the reason and information about the matcher and failing value. Example:
     *
     * <pre>
     *   assertThat(&quot;Help! Integers don't work&quot;, 0, is(1)); // fails:
     *     // failure message:
     *     // Help! Integers don't work
     *     // expected: is &lt;1&gt;
     *     // got value: &lt;0&gt;
     *   assertThat(&quot;Zero is one&quot;, 0, is(not(1))) // passes
     * </pre>
     *
     * <code>org.hamcrest.Matcher</code> does not currently document the meaning
     * of its type parameter <code>T</code>.  This method assumes that a matcher
     * typed as <code>Matcher&lt;T&gt;</code> can be meaningfully applied only
     * to values that could be assigned to a variable of type <code>T</code>.
     *
     * @param reason additional information about the error
     * @param <T> the static type accepted by the matcher (this can flag obvious
     * compile-time problems such as {@code assertThat(1, is("a"))}
     * @param actual the computed value being compared
     * @param matcher an expression, built of {@link Matcher}s, specifying allowed
     * values
     * @see org.hamcrest.CoreMatchers
     * @see org.hamcrest.MatcherAssert
     * @deprecated use {@code org.hamcrest.junit.MatcherAssert.assertThat()}
     */
    @Deprecated
    public static <T> void assertThat(String reason, T actual,
            Matcher<? super T> matcher) {
        MatcherAssert.assertThat(reason, actual, matcher);
    }

    /**
     * This interface facilitates the use of expectThrows from Java 8. It allows method references
     * to void methods (that declare checked exceptions) to be passed directly into expectThrows
     * without wrapping. It is not meant to be implemented directly.
     *
     * @since 4.13
     */
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    /**
     * Asserts that {@code runnable} throws an exception of type {@code expectedThrowable} when
     * executed. If it does not throw an exception, an {@link AssertionError} is thrown. If it
     * throws the wrong type of exception, an {@code AssertionError} is thrown describing the
     * mismatch; the exception that was actually thrown can be obtained by calling {@link
     * AssertionError#getCause}.
     *
     * @param expectedThrowable the expected type of the exception
     * @param runnable       a function that is expected to throw an exception when executed
     * @since 4.13
     */
    public static void assertThrows(Class<? extends Throwable> expectedThrowable, ThrowingRunnable runnable) {
        expectThrows(expectedThrowable, runnable);
    }

    /**
     * Asserts that {@code runnable} throws an exception of type {@code expectedThrowable} when
     * executed. If it does, the exception object is returned. If it does not throw an exception, an
     * {@link AssertionError} is thrown. If it throws the wrong type of exception, an {@code
     * AssertionError} is thrown describing the mismatch; the exception that was actually thrown can
     * be obtained by calling {@link AssertionError#getCause}.
     *
     * @param expectedThrowable the expected type of the exception
     * @param runnable       a function that is expected to throw an exception when executed
     * @return the exception thrown by {@code runnable}
     * @since 4.13
     */
    public static <T extends Throwable> T expectThrows(Class<T> expectedThrowable, ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable actualThrown) {
            if (expectedThrowable.isInstance(actualThrown)) {
                @SuppressWarnings("unchecked") T retVal = (T) actualThrown;
                return retVal;
            } else {
                String mismatchMessage = format("unexpected exception type thrown;",
                        expectedThrowable.getSimpleName(), actualThrown.getClass().getSimpleName());

                // The AssertionError(String, Throwable) ctor is only available on JDK7.
                AssertionError assertionError = new AssertionError(mismatchMessage);
                assertionError.initCause(actualThrown);
                throw assertionError;
            }
        }
        String message = String.format("expected %s to be thrown, but nothing was thrown",
                expectedThrowable.getSimpleName());
        throw new AssertionError(message);
    }
}
