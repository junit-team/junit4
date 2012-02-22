package junit.tests.runner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import junit.textui.ResultPrinter;
import junit.textui.TestRunner;

/**
 *  Test invoking a single test method of a TestCase.
 */
public class TextRunnerSingleMethodTest extends TestCase {
	
	static boolean fgWasInvoked;
	
	public static class InvocationTest extends TestCase {

		public void testWasInvoked() {
			TextRunnerSingleMethodTest.fgWasInvoked= true;
		}

		public void testNotInvoked() {
			fail("Shouldn't get here.");
		}
	}
	
	public void testSingle() throws Exception {
		TestRunner t= new TestRunner();
		t.setPrinter(new ResultPrinter(new PrintStream(new ByteArrayOutputStream())));
		String[] args= {
				"-m", "junit.tests.runner.TextRunnerSingleMethodTest$InvocationTest.testWasInvoked"
		};
		fgWasInvoked= false;
		t.start(args);
		assertTrue(fgWasInvoked);
	}

}