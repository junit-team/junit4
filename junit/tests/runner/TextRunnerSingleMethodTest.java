package junit.tests.runner;

import java.io.PrintStream;
import java.util.Enumeration;
import junit.framework.*;
import junit.textui.*;

/**
 *  Test invoking a single test method of a TestCase.
 */
public class TextRunnerSingleMethodTest extends TestCase {
	private final class NullResultPrinter extends ResultPrinter {
		private NullResultPrinter(PrintStream writer) {
			super(writer);
		}
		public void addError(Test test, Throwable t) {
		}
		public void addFailure(Test test, AssertionFailedError t) {
		}
		protected String elapsedTimeAsString(long runTime) {
			return null;
		}
		public void endTest(Test test) {
		}
		public PrintStream getWriter() {
			return null;
		}
		synchronized void print(TestResult result, long runTime) {
		}
		public void printDefect(TestFailure failure, int count) {
		}
		protected void printDefectHeader(TestFailure failure, int count) {
		}
		protected void printDefects(Enumeration failure, int count, String type) {
		}
		protected void printDefectTrace(TestFailure failure) {
		}
		protected void printErrors(TestResult result) {
		}
		protected void printFailures(TestResult result) {
		}
		protected void printFooter(TestResult result) {
		}
		protected void printHeader(long runTime) {
		}
		void printWaitPrompt() {
		}
		public void startTest(Test test) {
		}
	}

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
		TestRunner t= new TestRunner(new NullResultPrinter(null));
		String[] args= {
				"-m", "junit.tests.runner.TextRunnerSingleMethodTest$InvocationTest.testWasInvoked"
		};
		assertFalse(fgWasInvoked);
		t.start(args);
		assertTrue(fgWasInvoked);
	}

}