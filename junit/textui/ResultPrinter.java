
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

	void testStarted(String testName) {
		getWriter().print(".");
		if (fColumn++ >= 40) {
			getWriter().println();
			fColumn= 0;
		}
	}
	
	public void testFailed(int status, Test test, Throwable t) {
		switch (status) {
			case TestRunListener.STATUS_ERROR: getWriter().print("E"); break;
			case TestRunListener.STATUS_FAILURE: getWriter().print("F"); break;
		}
	}

	/**
	 * Prints failures to the standard output
	 */
	public synchronized void print(TestResult result, long runTime) {
		printTime(runTime);
	    printErrors(result);
	    printFailures(result);
	    printHeader(result);
	    getWriter().println();
	}
	/**
	 * Prints the errors to the standard output
	 */
	public void printErrors(TestResult result) {
	    if (result.errorCount() != 0) {
	        if (result.errorCount() == 1)
		        getWriter().println("There was "+result.errorCount()+" error:");
	        else
		        getWriter().println("There were "+result.errorCount()+" errors:");

			int i= 1;
			for (Enumeration e= result.errors(); e.hasMoreElements(); i++) {
			    TestFailure failure= (TestFailure)e.nextElement();
				getWriter().println(i+") "+failure.failedTest());
				getWriter().print(BaseTestRunner.getFilteredTrace(failure.trace()));
		    }
		}
	}
	/**
	 * Prints failures to the standard output
	 */
	public void printFailures(TestResult result) {
		if (result.failureCount() != 0) {
			if (result.failureCount() == 1)
				getWriter().println("There was " + result.failureCount() + " failure:");
			else
				getWriter().println("There were " + result.failureCount() + " failures:");
			int i = 1;
			for (Enumeration e= result.failures(); e.hasMoreElements(); i++) {
				TestFailure failure= (TestFailure) e.nextElement();
				getWriter().print(i + ") " + failure.failedTest());
				getWriter().print(BaseTestRunner.getFilteredTrace(failure.trace()));
			}
		}
	}
	/**
	 * Prints the header of the report
	 */
	public void printHeader(TestResult result) {
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
	}

	void printTime(long runTime) {
		getWriter().println();
		getWriter().println("Time: "+elapsedTimeAsString(runTime));
	}
	
	void printWaitPrompt() {
		getWriter().println();
		getWriter().println("<RETURN> to continue");
	}


	/**
	 * Returns the formatted string of the elapsed time.
	 * Duplicated from BaseTestRunner. Fix it.
	 */
	public String elapsedTimeAsString(long runTime) {
		return NumberFormat.getInstance().format((double)runTime/1000);
	}

	protected PrintStream getWriter() {
		return fWriter;
	}
}
