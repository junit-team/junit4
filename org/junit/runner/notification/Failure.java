package org.junit.runner.notification;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.runner.Description;

/**
 * A Test Failure holds the failed Test, method name, and the 
 * thrown exception
 */
public class Failure {
	private final Description fDescription;
	private Throwable fThrownException;

	/**
	 * Constructs a TestFailure with the given plan and exception.
	 */
	public Failure(Description description, Throwable thrownException) {
		fThrownException = thrownException;
		fDescription= description;
	}

	public String getTestHeader() {
		return fDescription.getDisplayName();
	}

	public Description getDescription() {
		return fDescription;
	}

	/**
	 * Gets the thrown exception.
	 */
	public Throwable getException() {
	    return fThrownException;
	}

	/**
	 * Returns a short description of the failure.
	 */
	@Override
	public String toString() {
	    StringBuffer buffer= new StringBuffer();
	    buffer.append(getTestHeader() + ": "+fThrownException.getMessage());
	    return buffer.toString();
	}

	public String getTrace() {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		getException().printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		return buffer.toString();
	}

	public String getMessage() {
		return getException().getMessage();
	}
}
