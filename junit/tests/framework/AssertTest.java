package junit.tests.framework;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class AssertTest extends TestCase {

	/* In the tests that follow, we can't use standard formatting
	 * for exception tests:
	 *     try {
	 *         somethingThatShouldThrow();
	 *         fail();
	 *     catch (AssertionFailedError e) {
	 *     }
	 * because fail() would never be reported.
	 */
	public void testFail() {
		// Also, we are testing fail, so we can't rely on fail() working.
		// We have to throw the exception manually, .
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
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
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
		} catch (AssertionFailedError e) {
			e.getMessage(); // why no assertion?
			return;
		}
		fail();
	}

	public void testAssertNull() {
		assertNull(null);
		try {
			assertNull(new Object());
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
	}

	public void testAssertNotNull() {
		assertNotNull(new Object());
		try {
			assertNotNull(null);
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
	}

	public void testAssertTrue() {
		assertTrue(true);
		try {
			assertTrue(false);
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
	}

	public void testAssertFalse() {
		assertFalse(false);
		try {
			assertFalse(true);
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
	}

	public void testAssertSame() {
		Object o= new Object();
		assertSame(o, o);
		try {
			assertSame(new Integer(1), new Integer(1));
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
	}

	public void testAssertNotSame() {
		assertNotSame(new Integer(1), null);
		assertNotSame(null, new Integer(1));
		assertNotSame(new Integer(1), new Integer(1));
		try {
			Integer obj= new Integer(1);
			assertNotSame(obj, obj);
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
	}

	public void testAssertNotSameFailsNull() {
		try {
			assertNotSame(null, null);
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
	}
}