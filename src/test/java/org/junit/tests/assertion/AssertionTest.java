package org.junit.tests.assertion;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.internal.ArrayComparisonFailure;

/**
 * Tests for {@link org.junit.Assert}
 */
public class AssertionTest {
// If you want to use 1.4 assertions, they will be reported correctly.
// However, you need to add the -ea VM argument when running.

// @Test (expected=AssertionError.class) public void error() {
//      assert false;
//  }

    private static final String ASSERTION_ERROR_EXPECTED = "AssertionError expected";

    @Test(expected = AssertionError.class)
    public void fails() {
        Assert.fail();
    }

    @Test
    public void failWithNoMessageToString() {
        try {
            Assert.fail();
        } catch (AssertionError exception) {
            assertEquals("java.lang.AssertionError", exception.toString());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void failWithMessageToString() {
        try {
            Assert.fail("woops!");
        } catch (AssertionError exception) {
            assertEquals("java.lang.AssertionError: woops!", exception.toString());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysNotEqual() {
        assertArrayEqualsFailure(
                new Object[]{"right"},
                new Object[]{"wrong"},
                "arrays first differed at element [0]; expected:<[right]> but was:<[wrong]>");
    }

    @Test
    public void arraysNotEqualWithMessage() {
        assertArrayEqualsFailure(
                "not equal",
                new Object[]{"right"},
                new Object[]{"wrong"},
                "not equal: arrays first differed at element [0]; expected:<[right]> but was:<[wrong]>");
    }

    @Test
    public void arraysExpectedNullMessage() {
        try {
            assertArrayEquals("not equal", null, new Object[]{new Object()});
        } catch (AssertionError exception) {
            assertEquals("not equal: expected array was null", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysActualNullMessage() {
        try {
            assertArrayEquals("not equal", new Object[]{new Object()}, null);
        } catch (AssertionError exception) {
            assertEquals("not equal: actual array was null", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysDifferentLengthDifferingAtStartMessage() {
        assertArrayEqualsFailure(
                "not equal",
                new Object[]{true},
                new Object[]{false, true},
                "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [0]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferentLengthDifferingAtEndMessage() {
        assertArrayEqualsFailure(
                "not equal",
                new Object[]{true},
                new Object[]{true, false},
                "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [1]; expected:<end of array> but was:<false>");
    }

    @Test
    public void arraysDifferentLengthDifferingAtEndAndExpectedArrayLongerMessage() {
        assertArrayEqualsFailure(
                "not equal",
                new Object[]{true, false},
                new Object[]{true},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<false> but was:<end of array>");
    }

    @Test
    public void arraysElementsDiffer() {
        assertArrayEqualsFailure(
                "not equal",
                new Object[]{"this is a very long string in the middle of an array"},
                new Object[]{"this is another very long string in the middle of an array"},
                "not equal: arrays first differed at element [0]; expected:<this is a[] very long string in...> but was:<this is a[nother] very long string in...>");
    }

    @Test
    public void arraysDifferAtElement0nullMessage() {
        assertArrayEqualsFailure(
                new Object[]{true},
                new Object[]{false},
                "arrays first differed at element [0]; expected:<true> but was:<false>"
        );
    }

    @Test
    public void arraysDifferAtElement1nullMessage() {
        assertArrayEqualsFailure(
                new Object[]{true, true},
                new Object[]{true, false},
                "arrays first differed at element [1]; expected:<true> but was:<false>"
        );
    }

    @Test
    public void arraysDifferAtElement0withMessage() {
        assertArrayEqualsFailure(
                "message",
                new Object[]{true},
                new Object[]{false},
                "message: arrays first differed at element [0]; expected:<true> but was:<false>"
        );
    }

    @Test
    public void arraysDifferAtElement1withMessage() {
        assertArrayEqualsFailure(
                "message",
                new Object[]{true, true},
                new Object[]{true, false},
                "message: arrays first differed at element [1]; expected:<true> but was:<false>"
        );
    }

    @Test
    public void multiDimensionalArraysAreEqual() {
        assertArrayEquals((new Object[][]{{true, true}, {false, false}}), (new Object[][]{{true, true}, {false, false}}));
    }

    @Test
    public void multiDimensionalIntArraysAreEqual() {
        int[][] int1 = {{1, 2, 3}, {4, 5, 6}};
        int[][] int2 = {{1, 2, 3}, {4, 5, 6}};
        assertArrayEquals(int1, int2);
    }

    @Test
    public void oneDimensionalPrimitiveArraysAreEqual() {
        assertArrayEquals(new boolean[]{true}, new boolean[]{true});
        assertArrayEquals(new byte[]{1}, new byte[]{1});
        assertArrayEquals(new char[]{1}, new char[]{1});
        assertArrayEquals(new short[]{1}, new short[]{1});
        assertArrayEquals(new int[]{1}, new int[]{1});
        assertArrayEquals(new long[]{1}, new long[]{1});
        assertArrayEquals(new double[]{1.0}, new double[]{1.0}, 1.0);
        assertArrayEquals(new float[]{1.0f}, new float[]{1.0f}, 1.0f);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalDoubleArraysAreNotEqual() {
        assertArrayEquals(new double[]{1.0}, new double[]{2.5}, 1.0);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalFloatArraysAreNotEqual() {
        assertArrayEquals(new float[]{1.0f}, new float[]{2.5f}, 1.0f);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalBooleanArraysAreNotEqual() {
        assertArrayEquals(new boolean[]{true}, new boolean[]{false});
    }

    @Test(expected = AssertionError.class)
    public void IntegerDoesNotEqualLong() {
        assertEquals(new Integer(1), new Long(1));
    }

    @Test
    public void intsEqualLongs() {
        assertEquals(1, 1L);
    }

    @Test
    public void multiDimensionalArraysDeclaredAsOneDimensionalAreEqual() {
        assertArrayEquals((new Object[]{new Object[]{true, true}, new Object[]{false, false}}), (new Object[]{new Object[]{true, true}, new Object[]{false, false}}));
    }

    @Test
    public void multiDimensionalArraysAreNotEqual() {
        assertArrayEqualsFailure(
                "message",
                new Object[][]{{true, true}, {false, false}},
                new Object[][]{{true, true}, {true, false}},
                "message: arrays first differed at element [1][0]; expected:<false> but was:<true>");
    }

    @Test
    public void multiDimensionalArraysAreNotEqualNoMessage() {
        assertArrayEqualsFailure(
                new Object[][]{{true, true}, {false, false}},
                new Object[][]{{true, true}, {true, false}},
                "arrays first differed at element [1][0]; expected:<false> but was:<true>");
    }

    @Test
    public void twoDimensionalArraysDifferentOuterLengthNotEqual() {
        assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{true}, {}},
                new Object[][]{{}},
                "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [0][0]; expected:<true> but was:<end of array>");
        assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}, {true}},
                new Object[][]{{}},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<java.lang.Object[1]> but was:<end of array>");
        assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}},
                new Object[][]{{true}, {}},
                "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [0][0]; expected:<end of array> but was:<true>");
        assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}},
                new Object[][]{{}, {true}},
                "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [1]; expected:<end of array> but was:<java.lang.Object[1]>");
    }

    @Test
    public void primitiveArraysConvertedToStringCorrectly() {
        assertArrayEqualsFailure(
                "not equal",
                new boolean[][]{{}, {true}},
                new boolean[][]{{}},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<boolean[1]> but was:<end of array>");
        assertArrayEqualsFailure(
                "not equal",
                new int[][]{{}, {23}},
                new int[][]{{}},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<int[1]> but was:<end of array>");
    }

    @Test
    public void twoDimensionalArraysConvertedToStringCorrectly() {
        assertArrayEqualsFailure(
                "not equal",
                new Object[][][]{{}, {{true}}},
                new Object[][][]{{}},
                "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<java.lang.Object[][1]> but was:<end of array>");
    }

    @Test
    public void twoDimensionalArraysDifferentInnerLengthNotEqual() {
        assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{true}, {}},
                new Object[][]{{}, {}},
                "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [0][0]; expected:<true> but was:<end of array>");
        assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}, {true}},
                new Object[][]{{}, {}},
                "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [1][0]; expected:<true> but was:<end of array>");
        assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}, {}},
                new Object[][]{{true}, {}},
                "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [0][0]; expected:<end of array> but was:<true>");
        assertArrayEqualsFailure(
                "not equal",
                new Object[][]{{}, {}},
                new Object[][]{{}, {true}},
                "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [1][0]; expected:<end of array> but was:<true>");
    }

    private void assertArrayEqualsFailure(Object[] expecteds, Object[] actuals, String expectedMessage) {
        try {
            assertArrayEquals(expecteds, actuals);
        } catch (ArrayComparisonFailure e) {
            assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    private void assertArrayEqualsFailure(String message, Object[] expecteds, Object[] actuals, String expectedMessage) {
        try {
            assertArrayEquals(message, expecteds, actuals);
        } catch (ArrayComparisonFailure e) {
            assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void multiDimensionalArraysDifferentLengthMessage() {
        try {
            assertArrayEquals("message", new Object[][]{{true, true}, {false, false}}, new Object[][]{{true, true}, {false}});
        } catch (AssertionError exception) {
            assertEquals("message: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1][1]; expected:<false> but was:<end of array>", exception.getMessage());
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void multiDimensionalArraysDifferentLengthNoMessage() {
        try {
            assertArrayEquals(new Object[][]{{true, true}, {false, false}}, new Object[][]{{true, true}, {false}});
        } catch (AssertionError exception) {
            assertEquals("array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1][1]; expected:<false> but was:<end of array>", exception.getMessage());
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysWithNullElementEqual() {
        Object[] objects1 = new Object[]{null};
        Object[] objects2 = new Object[]{null};
        assertArrayEquals(objects1, objects2);
    }

    @Test
    public void stringsDifferWithUserMessage() {
        try {
            assertEquals("not equal", "one", "two");
        } catch (ComparisonFailure exception) {
            assertEquals("not equal expected:<[one]> but was:<[two]>", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysEqual() {
        Object element = new Object();
        Object[] objects1 = new Object[]{element};
        Object[] objects2 = new Object[]{element};
        assertArrayEquals(objects1, objects2);
    }

    @Test
    public void arraysEqualWithMessage() {
        Object element = new Object();
        Object[] objects1 = new Object[]{element};
        Object[] objects2 = new Object[]{element};
        assertArrayEquals("equal", objects1, objects2);
    }

    @Test
    public void equals() {
        Object o = new Object();
        assertEquals(o, o);
        assertEquals("abc", "abc");
        assertEquals(true, true);
        assertEquals((byte) 1, (byte) 1);
        assertEquals('a', 'a');
        assertEquals((short) 1, (short) 1);
        assertEquals(1, 1); // int by default, cast is unnecessary
        assertEquals(1l, 1l);
        assertEquals(1.0, 1.0, 0.0);
        assertEquals(1.0d, 1.0d, 0.0d);
    }

    @Test(expected = AssertionError.class)
    public void notEqualsObjectWithNull() {
        assertEquals(new Object(), null);
    }

    @Test(expected = AssertionError.class)
    public void notEqualsNullWithObject() {
        assertEquals(null, new Object());
    }

    @Test
    public void notEqualsObjectWithNullWithMessage() {
        Object o = new Object();
        try {
            assertEquals("message", null, o);
        } catch (AssertionError e) {
            assertEquals("message expected:<null> but was:<" + o.toString() + ">", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notEqualsNullWithObjectWithMessage() {
        Object o = new Object();
        try {
            assertEquals("message", o, null);
        } catch (AssertionError e) {
            assertEquals("message expected:<" + o.toString() + "> but was:<null>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void objectsNotEquals() {
        assertEquals(new Object(), new Object());
    }

    @Test(expected = ComparisonFailure.class)
    public void stringsNotEqual() {
        assertEquals("abc", "def");
    }

    @Test(expected = AssertionError.class)
    public void booleansNotEqual() {
        assertEquals(true, false);
    }

    @Test(expected = AssertionError.class)
    public void bytesNotEqual() {
        assertEquals((byte) 1, (byte) 2);
    }

    @Test(expected = AssertionError.class)
    public void charsNotEqual() {
        assertEquals('a', 'b');
    }

    @Test(expected = AssertionError.class)
    public void shortsNotEqual() {
        assertEquals((short) 1, (short) 2);
    }

    @Test(expected = AssertionError.class)
    public void intsNotEqual() {
        assertEquals(1, 2);
    }

    @Test(expected = AssertionError.class)
    public void longsNotEqual() {
        assertEquals(1l, 2l);
    }

    @Test(expected = AssertionError.class)
    public void floatsNotEqual() {
        assertEquals(1.0, 2.0, 0.9);
    }

    @SuppressWarnings("deprecation")
    @Test(expected = AssertionError.class)
    public void floatsNotEqualWithoutDelta() {
        assertEquals(1.0, 1.1);
    }

    @Test
    public void floatsNotDoublesInArrays() {
        float delta = 4.444f;
        float[] f1 = new float[]{1.111f};
        float[] f2 = new float[]{5.555f};
        Assert.assertArrayEquals(f1, f2, delta);
    }

    @Test(expected = AssertionError.class)
    public void bigDecimalsNotEqual() {
        assertEquals(new BigDecimal("123.4"), new BigDecimal("123.0"));
    }


    @Test(expected = AssertionError.class)
    public void doublesNotEqual() {
        assertEquals(1.0d, 2.0d, 0.9d);
    }

    @Test
    public void naNsAreEqual() {
        assertEquals(Float.NaN, Float.NaN, Float.POSITIVE_INFINITY);
        assertEquals(Double.NaN, Double.NaN, Double.POSITIVE_INFINITY);
    }

    @SuppressWarnings("unused")
    @Test
    public void nullNullmessage() {
        try {
            assertNull("junit");
        } catch (AssertionError e) {
            assertEquals("expected null, but was:<junit>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @SuppressWarnings("unused")
    @Test
    public void nullWithMessage() {
        try {
            assertNull("message", "hello");
        } catch (AssertionError exception) {
            assertEquals("message expected null, but was:<hello>", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void same() {
        Object o1 = new Object();
        assertSame(o1, o1);
    }

    @Test
    public void notSame() {
        Object o1 = new Object();
        Object o2 = new Object();
        assertNotSame(o1, o2);
    }

    @Test(expected = AssertionError.class)
    public void objectsNotSame() {
        assertSame(new Object(), new Object());
    }

    @Test(expected = AssertionError.class)
    public void objectsAreSame() {
        Object o = new Object();
        assertNotSame(o, o);
    }

    @Test
    public void sameWithMessage() {
        try {
            assertSame("not same", "hello", "good-bye");
        } catch (AssertionError exception) {
            assertEquals("not same expected same:<hello> was not:<good-bye>",
                    exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void sameNullMessage() {
        try {
            assertSame("hello", "good-bye");
        } catch (AssertionError exception) {
            assertEquals("expected same:<hello> was not:<good-bye>", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notSameWithMessage() {
        Object o = new Object();
        try {
            assertNotSame("message", o, o);
        } catch (AssertionError exception) {
            assertEquals("message expected not same", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notSameNullMessage() {
        Object o = new Object();
        try {
            assertNotSame(o, o);
        } catch (AssertionError exception) {
            assertEquals("expected not same", exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessage() {
        try {
            fail(null);
        } catch (AssertionError exception) {
            // we used to expect getMessage() to return ""; see failWithNoMessageToString()
            assertNull(exception.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessageDisappearsWithStringAssertEquals() {
        try {
            assertEquals(null, "a", "b");
        } catch (ComparisonFailure e) {
            assertEquals("expected:<[a]> but was:<[b]>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessageDisappearsWithAssertEquals() {
        try {
            assertEquals(null, 1, 2);
        } catch (AssertionError e) {
            assertEquals("expected:<1> but was:<2>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void arraysDeclaredAsObjectAreComparedAsObjects() {
        Object a1 = new Object[]{"abc"};
        Object a2 = new Object[]{"abc"};
        assertEquals(a1, a2);
    }

    @Test
    public void implicitTypecastEquality() {
        byte b = 1;
        short s = 1;
        int i = 1;
        long l = 1L;
        float f = 1.0f;
        double d = 1.0;

        assertEquals(b, s);
        assertEquals(b, i);
        assertEquals(b, l);
        assertEquals(s, i);
        assertEquals(s, l);
        assertEquals(i, l);
        assertEquals(f, d, 0);
    }

    @Test
    public void errorMessageDistinguishesDifferentValuesWithSameToString() {
        try {
            assertEquals("4", new Integer(4));
        } catch (AssertionError e) {
            assertEquals("expected: java.lang.String<4> but was: java.lang.Integer<4>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatIncludesDescriptionOfTestedValueInErrorMessage() {
        String expected = "expected";
        String actual = "actual";

        String expectedMessage = "identifier\nExpected: \"expected\"\n     but: was \"actual\"";

        try {
            assertThat("identifier", actual, equalTo(expected));
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatIncludesAdvancedMismatch() {
        String expectedMessage = "identifier\nExpected: is an instance of java.lang.Integer\n     but: \"actual\" is a java.lang.String";

        try {
            assertThat("identifier", "actual", is(instanceOf(Integer.class)));
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatDescriptionCanBeElided() {
        String expected = "expected";
        String actual = "actual";

        String expectedMessage = "\nExpected: \"expected\"\n     but: was \"actual\"";

        try {
            assertThat(actual, equalTo(expected));
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullAndStringNullPrintCorrectError() {
        try {
            assertEquals(null, "null");
        } catch (AssertionError e) {
            assertEquals("expected: null<null> but was: java.lang.String<null>", e.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void stringNullAndNullWorksToo() {
        assertEquals("null", null);
    }

    private static class NullToString {
        @Override
        public String toString() {
            return null;
        }
    }

    @Test
    public void nullToString() {
        try {
            assertEquals(new NullToString(), new NullToString());
        } catch (AssertionError e) {
            assertEquals("expected: org.junit.tests.assertion.AssertionTest$NullToString<null> but "
                            + "was: org.junit.tests.assertion.AssertionTest$NullToString<null>",
                    e.getMessage());
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void compareBigDecimalAndInteger() {
        final BigDecimal bigDecimal = new BigDecimal("1.2");
        final Integer integer = Integer.valueOf("1");
        assertEquals(bigDecimal, integer);
    }

    @Test(expected = AssertionError.class)
    public void sameObjectIsNotEqual() {
        Object o = new Object();
        assertNotEquals(o, o);
    }

    @Test
    public void objectsWithDifferentReferencesAreNotEqual() {
        assertNotEquals(new Object(), new Object());
    }

    @Test
    public void assertNotEqualsIncludesCorrectMessage() {
        Integer value1 = new Integer(1);
        Integer value2 = new Integer(1);
        String message = "The values should be different";

        try {
            assertNotEquals(message, value1, value2);
        } catch (AssertionError e) {
            assertEquals(message + ". Actual: " + value1, e.getMessage());
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertNotEqualsIncludesTheValueBeingTested() {
        Integer value1 = new Integer(1);
        Integer value2 = new Integer(1);

        try {
            assertNotEquals(value1, value2);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains(value1.toString()));
            return;
        }

        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertNotEqualsWorksWithPrimitiveTypes() {
        assertNotEquals(1L, 2L);
        assertNotEquals("The values should be different", 1L, 2L);
        assertNotEquals(1.0, 2.0, 0);
        assertNotEquals("The values should be different", 1.0, 2.0, 0);
        assertNotEquals(1.0f, 2.0f, 0f);
        assertNotEquals("The values should be different", 1.0f, 2.0f, 0f);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsConsidersDeltaCorrectly() {
        assertNotEquals(1.0, 0.9, 0.1);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsConsidersFloatDeltaCorrectly() {
        assertNotEquals(1.0f, 0.75f, 0.25f);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsIgnoresDeltaOnNaN() {
        assertNotEquals(Double.NaN, Double.NaN, 1);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsIgnoresFloatDeltaOnNaN() {
        assertNotEquals(Float.NaN, Float.NaN, 1f);
    }

    @Test(expected = AssertionError.class)
    public void assertThrowsRequiresAnExceptionToBeThrown() {
        assertThrows(Throwable.class, nonThrowingRunnable());
    }

    @Test
    public void assertThrowsIncludesAnInformativeDefaultMessage() {
        try {
            assertThrows(Throwable.class, nonThrowingRunnable());
        } catch (AssertionError ex) {
            assertEquals("expected java.lang.Throwable to be thrown, but nothing was thrown", ex.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsIncludesTheSpecifiedMessage() {
        try {
            assertThrows("Foobar", Throwable.class, nonThrowingRunnable());
        } catch (AssertionError ex) {
            assertEquals(
                    "Foobar: expected java.lang.Throwable to be thrown, but nothing was thrown",
                    ex.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsReturnsTheSameObjectThrown() {
        NullPointerException npe = new NullPointerException();

        Throwable throwable = assertThrows(Throwable.class, throwingRunnable(npe));

        assertSame(npe, throwable);
    }

    @Test(expected = AssertionError.class)
    public void assertThrowsDetectsTypeMismatchesViaExplicitTypeHint() {
        NullPointerException npe = new NullPointerException();

        assertThrows(IOException.class, throwingRunnable(npe));
    }

    @Test
    public void assertThrowsWrapsAndPropagatesUnexpectedExceptions() {
        NullPointerException npe = new NullPointerException("inner-message");

        try {
            assertThrows(IOException.class, throwingRunnable(npe));
        } catch (AssertionError ex) {
            assertSame(npe, ex.getCause());
            assertEquals("inner-message", ex.getCause().getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsSuppliesACoherentErrorMessageUponTypeMismatch() {
        NullPointerException npe = new NullPointerException();

        try {
            assertThrows(IOException.class, throwingRunnable(npe));
        } catch (AssertionError error) {
            assertEquals("unexpected exception type thrown; expected:<java.io.IOException> but was:<java.lang.NullPointerException>",
                    error.getMessage());
            assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsSuppliesTheSpecifiedMessageUponTypeMismatch() {
        NullPointerException npe = new NullPointerException();

        try {
            assertThrows("Foobar", IOException.class, throwingRunnable(npe));
        } catch (AssertionError error) {
            assertEquals("Foobar: unexpected exception type thrown; expected:<java.io.IOException> but was:<java.lang.NullPointerException>",
                    error.getMessage());
            assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesCanonicalNameUponTypeMismatch() {
        NullPointerException npe = new NullPointerException();

        try {
            assertThrows(NestedException.class, throwingRunnable(npe));
        } catch (AssertionError error) {
            assertEquals(
                    "unexpected exception type thrown; expected:<org.junit.tests.assertion.AssertionTest.NestedException>"
                    + " but was:<java.lang.NullPointerException>",
                    error.getMessage());
            assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesNameUponTypeMismatchWithAnonymousClass() {
        NullPointerException npe = new NullPointerException() {
        };

        try {
            assertThrows(IOException.class, throwingRunnable(npe));
        } catch (AssertionError error) {
            assertEquals(
                    "unexpected exception type thrown; expected:<java.io.IOException>"
                    + " but was:<org.junit.tests.assertion.AssertionTest$1>",
                    error.getMessage());
            assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesCanonicalNameWhenRequiredExceptionNotThrown() {
        try {
            assertThrows(NestedException.class, nonThrowingRunnable());
        } catch (AssertionError error) {
            assertEquals(
                    "expected org.junit.tests.assertion.AssertionTest.NestedException to be thrown,"
                    + " but nothing was thrown", error.getMessage());
            return;
        }
        throw new AssertionError(ASSERTION_ERROR_EXPECTED);
    }

    private static class NestedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static ThrowingRunnable nonThrowingRunnable() {
        return new ThrowingRunnable() {
            public void run() throws Throwable {
            }
        };
    }

    private static ThrowingRunnable throwingRunnable(final Throwable t) {
        return new ThrowingRunnable() {
            public void run() throws Throwable {
                throw t;
            }
        };
    }
}
