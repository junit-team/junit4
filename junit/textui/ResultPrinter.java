
package junit.textui;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;
import junit.runner.TestRunListener;

public class ResultPrinter {
	PrintStream fWriter;
	int fColumn= 0;
	
	public ResultPrinter(PrintStream writer) {
		fWriter= writer;
	}
	
	/* API for use by textui.TestRunner
	 */

	void testStarted(String testName) {
		getWriter().print(".");
		if (fColumn++ >= 40) {
			getWriter().println();
			fColumn= 0;
		}
	}
	
	void testFailed(int status, Test test, Throwable t) {
		switch (status) {
			case TestRunListener.STATUS_ERROR: getWriter().print("E"); break;
			case TestRunListener.STATUS_FAILURE: getWriter().print("F"); break;
		}
	}

	synchronized void print(TestResult result, long runTime) {
		printHeader(runTime);
	    printErrors(result);
	    printFailures(result);
	    printFooter(result);
	}

	void printWaitPrompt() {
		getWriter().println();
		getWriter().println("<RETURN> to continue");
	}
	
	/* Internal methods 
	 */

	protected void printHeader(long runTime) {
		getWriter().println();
		getWriter().println("Time: "+elapsedTimeAsString(runTime));
	}
	
	protected void printErrors(TestResult result) {
		printBooBoos(result.errors(), result.errorCount(), "error");
	}
	
	protected void printFailures(TestResult result) {
		printBooBoos(result.failures(), result.failureCount(), "failure");
	}
	
	protected void printBooBoos(Enumeration booBoos, int count, String type) {
		if (count == 0) return;
		if (count == 1)
			getWriter().println("There was " + count + " " + type + ":");
		else
			getWriter().println("There were " + count + " " + type + "s:");
		for (int i= 1; booBoos.hasMoreElements(); i++) {
			printBooBoo((TestFailure) booBoos.nextElement(), i);
		}
	}
	
	protected void printBooBoo(TestFailure booBoo,int count) {
		getWriter().print(count + ") " + booBoo.failedTest());
		getWriter().print(BaseTestRunner.getFilteredTrace(booBoo.trace()));
	}

	protected void printFooter(TestResult result) {
		if (result.wasSuccessful()) {
			getWriter().println();
			getWriter().print("OK");
			getWriter().println (" (" + result.runCount() + " test" + (result.runCount() == 1 ? "": "s") + ")");

		} else {
			getWriter().println();
			getWriter().println("FAILURES!!!");
			getWriter().println("Tests run: "+result.runCount()+ 
				         ",  Failures: "+result.failureCount()+
				         ",  Errors: "+result.errorCount());
		}
	    getWriter().println();
	}


	/**
	 * Returns the formatted string of the elapsed time.
	 * Duplicated from BaseTestRunner. Fix it.
	 */
	protected String elapsedTimeAsString(long runTime) {
		return NumberFormat.getInstance().format((double)runTime/1000);
	}

	public PrintStream getWriter() {
		return fWriter;
	}
}
