package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import junit.framework.JUnit4TestAdapter;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;

public class AssertionTest {
// If you want to use 1.4 assertions, they will be reported correctly.
// However, you need to add the -ea VM argument when running.
	
//	@Test @Expected(AssertionError.class) public void error() {
//		assert false;
//	}
	
	@Test(expected= AssertionError.class) public void fails() {
		Assert.fail();
	}
	
	@Test(expected= AssertionError.class) public void arraysNotEqual() {
		assertEquals(new Object[] {new Object()}, new Object[] {new Object()});
	}
	
	@Test(expected= AssertionError.class) public void arraysNotEqualWithMessage() {
		assertEquals("not equal", new Object[] {new Object()}, new Object[] {new Object()});
	}
	
	@Test public void arraysExpectedNullMessage() {
		try {
			assertEquals("not equal", null, new Object[] {new Object()});
		} catch (AssertionError exception) {
			assertEquals("not equal: expected array was null", exception.getMessage());
		}
	}
	
	@Test public void arraysActualNullMessage() {
		try {
			assertEquals("not equal", new Object[] {new Object()}, null);
		} catch (AssertionError exception) {
			assertEquals("not equal: actual array was null", exception.getMessage());
		}
	}
	
	@Test public void arraysDifferentLengthMessage() {
		try {
			assertEquals("not equal", new Object[0] , new Object[1]);
		} catch (AssertionError exception) {
			assertEquals("not equal: array lengths differed, expected.length=0 actual.length=1", exception.getMessage());
		}
	}
	
	@Test(expected=ComparisonFailure.class) public void arraysElementsDiffer() {
		assertEquals("not equal", new Object[] {"this is a very long string in the middle of an array"} , new Object[] {"this is another very long string in the middle of an array"});
	}

    @Test public void arraysDifferAtElement0nullMessage() {
		try {
			assertEquals(new Object[] { true }, new Object[] { false });
		} catch (AssertionError exception) {
			assertEquals("arrays first differed at element [0]; expected:<true> but was:<false>", exception
					.getMessage());
		}
	}

	@Test public void arraysDifferAtElement1nullMessage() {
		try {
			assertEquals(new Object[] { true, true }, new Object[] { true,
					false });
		} catch (AssertionError exception) {
			assertEquals("arrays first differed at element [1]; expected:<true> but was:<false>", exception
					.getMessage());
		}
	}
	
    @Test public void arraysDifferAtElement0withMessage() {
		try {
			assertEquals("message", new Object[] { true }, new Object[] { false });
		} catch (AssertionError exception) {
			assertEquals("message: arrays first differed at element [0]; expected:<true> but was:<false>", exception
					.getMessage());
		}
	}

	@Test public void arraysDifferAtElement1withMessage() {
		try {
			assertEquals("message", new Object[] {true, true}, new Object[] {true, false});
			fail();
		} catch (AssertionError exception) {
			assertEquals("message: arrays first differed at element [1]; expected:<true> but was:<false>", exception.getMessage());
		}
	}
	
	@Test public void multiDimensionalArraysAreEqual() {
		assertEquals(new Object[][]{{true, true}, {false, false}}, new Object[][]{{true, true}, {false, false}});
	}
	
	@Ignore("Too much to do to get this working for 4.0 release")
	@Test public void multiDimensionalArraysAreNotEqual() {
		try {
			assertEquals("message", new Object[][]{{true, true}, {false, false}}, new Object[][]{{true, true}, {false, true}});
			fail();
		} catch (AssertionError exception) {
			assertEquals("message: arrays first differed at element [1][1]; expected:<false> but was:<true>", exception.getMessage());
		}
			
	}
	
	@Test public void stringsDifferWithUserMessage() {
		try {
			assertEquals("not equal", "one", "two");
		} catch (Throwable exception) {
			assertEquals("not equal expected:<[one]> but was:<[two]>", exception.getMessage());
		}
	}
	
	
	@Test public void arraysEqual() {
		Object element= new Object();
		Object[] objects1= new Object[] {element};
		Object[] objects2= new Object[] {element};
		assertEquals(objects1, objects2);
	}
	
	@Test public void arraysEqualWithMessage() {
		Object element= new Object();
		Object[] objects1= new Object[] {element};
		Object[] objects2= new Object[] {element};
		assertEquals("equal", objects1, objects2);
	}
	
	@Test public void equals() {
		Object o= new Object();
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
	
	@Test(expected= AssertionError.class) public void objectsNotEquals() {
		assertEquals(new Object(), new Object());
	}
	
	@Test(expected= ComparisonFailure.class) public void stringsNotEqual() {
		assertEquals("abc", "def");
	}
	
	@Test(expected= AssertionError.class) public void booleansNotEqual() {
		assertEquals(true, false);
	}
	
	@Test(expected= AssertionError.class) public void bytesNotEqual() {
		assertEquals((byte) 1, (byte) 2);
	}
	
	@Test(expected= AssertionError.class) public void charsNotEqual() {
		assertEquals('a', 'b');
	}
	
	@Test(expected= AssertionError.class) public void shortsNotEqual() {
		assertEquals((short) 1, (short) 2);
	}
	
	@Test(expected= AssertionError.class) public void intsNotEqual() {
		assertEquals(1, 2);
	}
	
	@Test(expected= AssertionError.class) public void longsNotEqual() {
		assertEquals(1l, 2l);
	}
	
	@Test(expected= AssertionError.class) public void floatsNotEqual() {
		assertEquals(1.0, 2.0, 0.9);
	}
	
	@Test(expected= AssertionError.class) public void doublesNotEqual() {
		assertEquals(1.0d, 2.0d, 0.9d);
	}
	
	@Test public void naNsAreEqual() {
		assertEquals(Float.NaN, Float.NaN, Float.POSITIVE_INFINITY);
		assertEquals(Double.NaN, Double.NaN, Double.POSITIVE_INFINITY);
	}
	
	@Test public void same() {
		Object o1= new Object();
		assertSame(o1, o1);
	}

	@Test public void notSame() {
		Object o1= new Object();
		Object o2= new Object();
		assertNotSame(o1, o2);
	}

	@Test(expected= AssertionError.class) public void objectsNotSame() {
		assertSame(new Object(), new Object());
	}

	@Test(expected= AssertionError.class) public void objectsAreSame() {
		Object o= new Object();
		assertNotSame(o, o);
	}

	@Test public void sameWithMessage() {
		try {
			assertSame("not same", "hello", "good-bye");
		} catch (AssertionError exception) {
			assertEquals("not same expected same:<hello> was not:<good-bye>",
					exception.getMessage());
		}
	}

	@Test public void sameNullMessage() {
		try {
			assertSame("hello", "good-bye");
		} catch (AssertionError exception) {
			assertEquals("expected same:<hello> was not:<good-bye>", exception
					.getMessage());
		}
	}

	@Test public void notSameWithMessage() {
		try {
			assertNotSame("not same", "hello", "good-bye");
		} catch (AssertionError exception) {
			assertEquals("not same expected not same", exception.getMessage());
		}
	}

	@Test public void notSameNullMessage() {
		try {
			assertNotSame("hello", "good-bye");
		} catch (AssertionError exception) {
			assertEquals("expected not same", exception.getMessage());
		}
	}

	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(AssertionTest.class);
	}
}
