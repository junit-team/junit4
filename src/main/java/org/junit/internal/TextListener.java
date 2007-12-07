package org.junit.internal;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TextListener extends RunListener {

	private final PrintStream fWriter;

	public TextListener(JUnitSystem system) {
		this(system.out());
	}

	public TextListener(PrintStream writer) {
		this.fWriter= writer;
	}

	@Override
	public void testRunFinished(Result result) {
		printHeader(result.getRunTime());
		printFailures(result);
		printIgnorances(result);
		printFooter(result);
	}

	@Override
	public void testStarted(Description description) {
		fWriter.append('.');
	}

	@Override
	public void testFailure(Failure failure) {
		fWriter.append('E');
	}
	
	@Override
	public void testIgnored(Description description) {
		fWriter.append('I');
	}
	
	/*
	 * Internal methods
	 */

	private PrintStream getWriter() {
		return fWriter;
	}

	protected void printHeader(long runTime) {
		getWriter().println();
		getWriter().println("Time: " + elapsedTimeAsString(runTime));
	}

	protected void printFailures(Result result) {
		printExceptions("failure", "", result.getFailures());
	}
	
	private void printIgnorances(Result result) {
		printExceptions("ignored test", "IGNORED TEST ", result.getIgnorances());
	}

	private void printExceptions(String exceptionTypeName, String listPrefix,
			List<? extends Failure> exceptions) {
		if (exceptions.size() == 0)
			return;
		if (exceptions.size() == 1)
			getWriter().println("There was " + exceptions.size() + " " + exceptionTypeName + ":");
		else
			getWriter().println("There were " + exceptions.size() + " " + exceptionTypeName + "s:");
		int i= 1;
		for (Failure each : exceptions)
			printFailure(each, listPrefix + i++);
	}

	protected void printFailure(Failure failure, String prefix) {
		getWriter().println(prefix + ") " + failure.getTestHeader());
		getWriter().print(failure.getTrace());
	}

	protected void printFooter(Result result) {
		if (result.wasSuccessful()) {
			getWriter().println();
			getWriter().print("OK");
			getWriter().println(" (" + result.getRunCount() + " test" + (result.getRunCount() == 1 ? "" : "s") + ")");

		} else {
			getWriter().println();
			getWriter().println("FAILURES!!!");
			getWriter().println("Tests run: " + result.getRunCount() + ",  Failures: " + result.getFailureCount());
		}
		getWriter().println();
	}

	/**
	 * Returns the formatted string of the elapsed time. Duplicated from
	 * BaseTestRunner. Fix it.
	 */
	protected String elapsedTimeAsString(long runTime) {
		return NumberFormat.getInstance().format((double) runTime / 1000);
	}
}
