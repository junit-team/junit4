package org.junit.samples;

import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Some simple tests.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class SimpleAnnotatedTest {
    protected int fValue1;
    protected int fValue2;

    @Before
    public void setUp() {
        fValue1 = 2;
        fValue2 = 3;
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SimpleAnnotatedTest.class);
    }

    public int unused;

    @Test
    public void divideByZero() {
        int zero = 0;
        int result = 8 / zero;
        unused = result; // avoid warning for not using result
    }

    @Test
    public void testEquals() {
        assertEquals(12, 12);
        assertEquals(12L, 12L);
        assertEquals(new Long(12), new Long(12));

        assertEquals("Size", 12, 13);
        assertEquals("Capacity", 12.0, 11.99, 0.0);
    }

}