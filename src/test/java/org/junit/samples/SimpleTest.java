package org.junit.samples;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;

/**
 * Some simple tests.
 *
 */
public class SimpleTest  {
	protected int fValue1;
	protected int fValue2;

	@Before public void setUp() {
		fValue1= 2;
		fValue2= 3;
	}
	
	public static junit.framework.Test suite() {
		 return new JUnit4TestAdapter(SimpleTest.class);
	}

	public int unused;
	@Test public void divideByZero() {
		int zero= 0;
		int result= 8/zero;
		unused= result; // avoid warning for not using result
	}
	
	@Test public void testEquals() {
		assertEquals(12, 12);
		assertEquals(12L, 12L);
		assertEquals(new Long(12), new Long(12));

		assertEquals("Size", 12, 13);
		assertEquals("Capacity", 12.0, 11.99, 0.0);
	}

}