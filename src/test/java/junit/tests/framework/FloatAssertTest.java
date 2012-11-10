package junit.tests.framework;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class FloatAssertTest extends TestCase {

    /**
     * Test for the special Double.NaN value.
     */
    public void testAssertEqualsNaNFails() {
        try {
            assertEquals(1.234f, Float.NaN, 0.0);
            fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertNaNEqualsFails() {
        try {
            assertEquals(Float.NaN, 1.234f, 0.0);
            fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertNaNEqualsNaN() {
        assertEquals(Float.NaN, Float.NaN, 0.0);
    }

    public void testAssertPosInfinityNotEqualsNegInfinity() {
        try {
            assertEquals(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 0.0);
            fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertPosInfinityNotEquals() {
        try {
            assertEquals(Float.POSITIVE_INFINITY, 1.23f, 0.0);
            fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertPosInfinityEqualsInfinity() {
        assertEquals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, 0.0);
    }

    public void testAssertNegInfinityEqualsInfinity() {
        assertEquals(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, 0.0);
    }

    public void testAllInfinities() {
        try {
            assertEquals(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
            fail();
        } catch (AssertionFailedError e) {
        }
    }

}
