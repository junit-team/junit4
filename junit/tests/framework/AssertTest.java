package junit.tests.framework;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class AssertTest extends TestCase {
	
	public void testFail() {
		// We can't use the standard formatting of an exception test
		// (i.e. throwing right after the call to fail()
		// because we would catch the AssertionFailedError.
		// Also, we are testing fail, so we have to throw the exception
		// manually, we can't rely on fail() working.
		try {
			fail();
		} catch (AssertionFailedError e) {
			return;
		}
		throw new AssertionFailedError();
	}

	public void testAssertEquals() {
		Object o= new Object();
		assertEquals(o, o);
		try {
			assertEquals(new Object(), new Object());
			fail();
		} catch (AssertionFailedError e) {
		}
	}

	public void testAssertEqualsNull() {
		assertEquals(null, null);
	}

	public void testAssertStringEquals() {
		assertEquals("a", "a");
	}

	public void testAssertNullNotEqualsString() {
		try {
			assertEquals(null, "foo");
			fail();
		} catch (ComparisonFailure e) {
		}
	}

	public void testAssertStringNotEqualsNull() {
		try {
			assertEquals("foo", null);
			fail();
		} catch (ComparisonFailure e) {
			e.getMessage(); // why no assertion?
		}
	}

	public void testAssertNullNotEqualsNull() {
		try {
			assertEquals(null, new Object());
			fail();
		} catch (AssertionFailedError e) {
			e.getMessage(); // why no assertion?
		}
	}

	public void testAssertNull() {
		assertNull(null);
		try {
			assertNull(new Object());
			fail();
		} catch (AssertionFailedError e) {
		}
	}

	public void testAssertNotNull() {
		assertNotNull(new Object());
		try {
			assertNotNull(null);
			fail();
		} catch (AssertionFailedError e) {
		}
	}

	public void testAssertTrue() {
		assertTrue(true);
		try {
			assertTrue(false);
			fail();
		} catch (AssertionFailedError e) {
		}
	}

	public void testAssertFalse() {
		assertFalse(false);
		try {
			assertFalse(true);
			fail();
		} catch (AssertionFailedError e) {
		}
	}

	public void testAssertSame() {
		Object o= new Object();
		assertSame(o, o);
		try {
			assertSame(new Integer(1), new Integer(1));
			fail();
		} catch (AssertionFailedError e) {
		}
	}

	public void testAssertNotSame() {
		assertNotSame(new Integer(1), null);
		assertNotSame(null, new Integer(1));
		assertNotSame(new Integer(1), new Integer(1));
		try {
			Integer obj= new Integer(1);
			assertNotSame(obj, obj);
			fail();
		} catch (AssertionFailedError e) {
		}
	}

	public void testAssertNotSameFailsNull() {
		try {
			assertNotSame(null, null);
			fail();
		} catch (AssertionFailedError e) {
		}
	}
}