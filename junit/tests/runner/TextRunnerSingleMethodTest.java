package junit.tests.runner;

import junit.framework.*;
import junit.textui.*;

/**
 *  Test invoking a single test method of a TestCase.
 */
public class TextRunnerSingleMethodTest extends TestCase {
	public static class InvocationTest extends TestCase {

		public void testWasInvoked() {
			TextRunnerSingleMethodTest.fgWasInvoked= true;
		}

		public void testNotInvoked() {
			fail("Shouldn't get here.");
		}
	}
	static boolean fgWasInvoked= false;

	public void testSingle() throws Exception {
		TestRunner t= new TestRunner();
		String[] args= {
				"-m", "junit.tests.runner.TextRunnerSingleMethodTest$InvocationTest.testWasInvoked"
		};
		assertFalse(fgWasInvoked);
		t.start(args);
		assertTrue(fgWasInvoked);
	}

}