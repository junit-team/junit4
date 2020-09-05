package junit.tests.framework;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class DoublePrecisionAssertTest extends TestCase {

    /**
     * Test for the special Double.NaN value.
     */
    public void testAssertEqualsNaNFails() {
        try {
            assertEquals(1.234, Double.NaN, 0.0);
        } catch (AssertionFailedError e) {
            return;
        }
        fail();
    }

    public void testAssertNaNEqualsFails() {
        try {
            assertEquals(Double.NaN, 1.234, 0.0);
        } catch (AssertionFailedError e) {
            return;
        }
        fail();
    }

    public void testAssertNaNEqualsNaN() {
        assertEquals(Double.NaN, Double.NaN, 0.0);
    }

    public void testAssertPosInfinityNotEqualsNegInfinity() {
        try {
            assertEquals(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0);
        } catch (AssertionFailedError e) {
            return;
        }
        fail();
    }

    public void testAssertPosInfinityNotEquals() {
        try {
            assertEquals(Double.POSITIVE_INFINITY, 1.23, 0.0);
        } catch (AssertionFailedError e) {
            return;
        }
        fail();
    }

    public void testAssertPosInfinityEqualsInfinity() {
        assertEquals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0);
    }

    public void testAssertNegInfinityEqualsInfinity() {
        assertEquals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0);
    }

}
