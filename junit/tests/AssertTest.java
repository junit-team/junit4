package junit.tests;

import junit.framework.*;

public class AssertTest extends TestCase {
	public AssertTest(String name) {
		super(name);
	}
	
	/**
  	 * Test for the special Double.NaN value.
  	 */
 	public void testAssertEqualsNaNFails() {
 		try {
     		assertEquals(1.234, Double.NaN, 0.00001);
 		} catch (AssertionFailedError e) {
 			return;
  		}
  		fail();
 	}

 	public void testAssertNaNEqualsFails() {
 		try {
     		assertEquals(Double.NaN, 1.234, 0.00001);
 		} catch (AssertionFailedError e) {
 			return;
  		}
  		fail();
 	}

 	public void testAssertNaNEqualsNaNFails() {
 		try {
     		assertEquals(Double.NaN, Double.NaN, 0.00001);
 		} catch (AssertionFailedError e) {
 			return;
  		}
  		fail();
 	}

	public void testAssertEquals() {
		Object o= new Object();
		assertEquals(o, o);
	}
	
	public void testAssertEqualsNull() {
		assertEquals(null, null);
	}
	
	public void testAssertNull() {
		assertNull(null);
	}
	
	public void testAssertNullNotEqualsNull() {
 		try {
     		assertEquals(null, new Object());
 		} catch (AssertionFailedError e) {
 			return;
  		}
  		fail();
	}
	
	public void testAssertSame() {
		Object o= new Object();
		assertSame(o, o);
	}
	
	public void testAssertSameFails() {
 		try {
     		assertSame(new Integer(1), new Integer(1));
    	} catch (AssertionFailedError e) {
 			return;
  		}
  		fail();
	}

	public void testFail() {
 		try {
     		fail();
    	} catch (AssertionFailedError e) {
 			return;
  		}
  		throw new AssertionFailedError(); // You can't call fail() here
	}
	
	public void testFailAssertNotNull() {
 		try {
     		assertNotNull(null);
      	} catch (AssertionFailedError e) {
 			return;
  		}
  		fail();
	}
	
	public void testSucceedAssertNotNull() {
		assertNotNull(new Object());
	}
}