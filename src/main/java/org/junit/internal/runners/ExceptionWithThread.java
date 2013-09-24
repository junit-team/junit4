package org.junit.internal.runners;

import java.text.MessageFormat;

/**
 * An Exception that also carries information about some other relevant thread than
 * the one whose stack trace is stored in the exception.
 */
public class ExceptionWithThread extends Exception {
	
	private Thread fThread;
	private StackTraceElement[] fStack;
	private String fDescription;
	
	/**
	 * Constructs a new exception with the detail message and relevant thread.
	 * @param message The detail message (as for an {@link Exception}).
	 * @param thread The relevant thread.
	 */
	public ExceptionWithThread (String message, Thread thread) {
		this (message, thread, null);
	}

	/**
	 * Constructs a new exception with the detail message, relevant thread, and
	 * a description explaining why the thread is relevant.
	 * @param message The detail message (as for an {@link Exception}).
	 * @param thread The relevant thread.
	 * @param description A format string (used by {@link MessageFormat#format(Object)})
	 * that describes why the thread is relevant.  {@code {0}} in the format string is
	 * replaced by the thread name.
	 */
	public ExceptionWithThread (String message, Thread thread, String description) {
		super(message);
		fThread = thread;
		try {
			fStack = thread.getStackTrace();
		} catch (SecurityException e) {
			fStack = new StackTraceElement[0];
		}
		fDescription = (description == null) ? null :
			MessageFormat.format(description, thread.getName());
	
	}

	/**
	 * Returns the relevant thread for the exception.
	 * @return The relevant thread.
	 */
	public Thread getThread () { return fThread; }
	
	/**
	 * Returns the stack trace of the relevant thread.
	 * @return The stack trace of the relevant thread, at the point when the
	 * {@link ExceptionWithThread} was constructed; may have length 0 if the 
	 * stack trace could not be determined (e.g. the thread terminated before the
	 * exception was created).
	 */
	public StackTraceElement[] getThreadStackTrace() { return fStack; }
	
	/**
	 * Returns a description of why the thread is relevant.
	 * @return A description of why the thread is relevant, or {@code null} if the
	 * exception was created without a description.  If a description was provided,
	 * the sequence {@code {0}} in the description is replaced by the name of the thread.
	 */
	public String getDescription() { return fDescription; }

}
