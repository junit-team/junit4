package org.junit;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.junit.internal.ArrayComparisonFailure;

/**
 * Assertions on arrays.
 *
 * @since 4.12
 */
class ArraysAssert {
    private ArraysAssert() {
    }

    /**
     * Asserts that two boolean arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds boolean array with expected values
     * @param actuals boolean array with expected values
     */
    public static void assertEquals(String message, final boolean[] expecteds, final boolean[] actuals)
            throws ArrayComparisonFailure {
        if (Arrays.equals(expecteds, actuals)) {
            return; // fast path
        }

        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public void assertElementEquals(int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
        });
    }

    /**
     * Asserts that two byte arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds byte array with expected values
     * @param actuals byte array with expected values
     */
    public static void assertEquals(String message, final byte[] expecteds, final byte[] actuals)
            throws ArrayComparisonFailure {
        if (Arrays.equals(expecteds, actuals)) {
            return; // fast path
        }

        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public void assertElementEquals(int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
        });
    }

    /**
     * Asserts that two char arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds char array with expected values
     * @param actuals char array with expected values
     */
    public static void assertEquals(String message, final char[] expecteds, final char[] actuals)
            throws ArrayComparisonFailure {
        if (Arrays.equals(expecteds, actuals)) {
            return; // fast path
        }

        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public void assertElementEquals(int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
        });
    }

    /**
     * Asserts that two short arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds short array with expected values
     * @param actuals short array with expected values
     */
    public static void assertEquals(String message, final short[] expecteds, final short[] actuals) {
        if (Arrays.equals(expecteds, actuals)) {
            return; // fast path
        }

        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public void assertElementEquals(int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
        });
    }

    /**
     * Asserts that two int arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds int array with expected values
     * @param actuals int array with expected values
     */
    public static void assertEquals(String message, final int[] expecteds, final int[] actuals)
            throws ArrayComparisonFailure {
        if (Arrays.equals(expecteds, actuals)) {
            return; // fast path
        }

        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public void assertElementEquals(int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
        });
    }

    /**
     * Asserts that two long arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds long array with expected values
     * @param actuals long array with expected values
     */
    public static void assertEquals(String message, final long[] expecteds, final long[] actuals)
            throws ArrayComparisonFailure {
        if (Arrays.equals(expecteds, actuals)) {
            return; // fast path
        }

        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public void assertElementEquals(int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
        });
    }

    /**
     * Asserts that two double arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds double array with expected values
     * @param actuals double array with expected values
     * @param delta the maximum delta between {@code expecteds[i]} and
     *         {@code actuals[i]} for which both numbers are still
     *         considered equal
     */
    public static void assertEquals(
            String message, final double[] expecteds, final double[] actuals, final double delta) {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public void assertElementEquals(int index) {
                        Assert.assertEquals(expecteds[index], actuals[index], delta);
                    }
        });
    }

    /**
     * Asserts that two float arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds float array with expected values
     * @param actuals float array with expected values
     * @param delta the maximum delta between {@code expecteds[i]} and
     *         {@code actuals[i]} for which both numbers are still
     *         considered equal
     */
    public static void assertEquals(
            String message, final float[] expecteds, final float[] actuals, final float delta) {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public void assertElementEquals(int index) {
                        Assert.assertEquals(expecteds[index], actuals[index], delta);
                    }
        });
    }

    /**
     * Asserts that two object arrays are deeply equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null},
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (can be {@code null})
     * @param expecteds object array with expected values
     * @param actuals object array with expected values
     */
    public static void assertDeepEquals(
            String message, Object[] expecteds, Object[] actuals) {
        if (Arrays.deepEquals(expecteds, actuals)) {
            return; // fast path
        }

        internalAssertEquals(message, expecteds, actuals,
                new ObjectArrayComparisonHelper(message, expecteds, actuals));
    }
 

    private static void assertDeepEqualsViaReflection(
            String message, Object expecteds, Object actuals) {
        if (Arrays.deepEquals(new Object[] { expecteds }, new Object[] { actuals })) {
            return; // fast path
        }

        internalAssertEquals(message, expecteds, actuals,
                new ObjectArrayComparisonHelper(message, expecteds, actuals));
    }

    /**
     * {@link ComparisonHelper} for working with Object arrays.
     */
    private static class ObjectArrayComparisonHelper implements ComparisonHelper {
        private final String message;
        private final Object expecteds;
        private final Object actuals;

        public ObjectArrayComparisonHelper(
                String message, Object expecteds, Object actuals) {
            this.message = message;
            this.expecteds = expecteds;
            this.actuals = actuals;
        }
 
        public final void assertElementEquals(int index) {
            Object expected = Array.get(expecteds, index);
            Object actual = Array.get(actuals, index);

            if (isArray(expected) && isArray(actual)) {
                try {
                    assertDeepEqualsViaReflection(message, expected, actual);
                } catch (ArrayComparisonFailure e) {
                    e.addDimension(index);
                    throw e;
                }
            } else {
                Assert.assertEquals(expected, actual);
            }
        }
    }

    private static boolean isArray(Object expected) {
        return expected != null && expected.getClass().isArray();
    }

    private static void internalAssertEquals(
            String message, Object expecteds, Object actuals, ComparisonHelper comparisonHelper) {
        String messagePrefix = message == null ? "" : message + ": ";

        if (!arraysTriviallyEqual(messagePrefix, expecteds, actuals)) {
            assertElementsEqual(messagePrefix, expecteds, actuals, comparisonHelper);
        }
    }

    private static boolean arraysTriviallyEqual(String messagePrefix,
            Object expecteds, Object actuals) {
        if (expecteds == actuals) {
            return true;
        }
        if (expecteds == null) {
            Assert.fail(messagePrefix + "expected array was null");
        }
        if (actuals == null) {
            Assert.fail(messagePrefix + "actual array was null");
        }
        return false;
    }

    private static void assertElementsEqual(
            String messagePrefix, Object expecteds, Object actuals,
            ComparisonHelper comparisonHelper) {
        int actualsLength = Array.getLength(actuals);
        int expectedsLength = Array.getLength(expecteds);
        if (actualsLength != expectedsLength) {
            Assert.fail(messagePrefix + "array lengths differed, expected.length="
                    + expectedsLength + " actual.length=" + actualsLength);
        }
        int index = 0;
        try {
            for ( ; index < expectedsLength; index++) {
                comparisonHelper.assertElementEquals(index);
            }
        } catch (ArrayComparisonFailure e) {
            /*
             * If we get here, the above assertElementEquals() was comparing two arrays.
             * Re-throw the exception so the caller can optionally add dimensions.
             */
            throw e;
        } catch (AssertionError e) {
            throw new ArrayComparisonFailure(messagePrefix, e, index);
        }
    }

    /**
     * Provides generic access to the arrays being compared.
     */
    interface ComparisonHelper {

        /**
         * Asserts that the two elements at the given index are equal.
         */
        void assertElementEquals(int index);
    }
}
