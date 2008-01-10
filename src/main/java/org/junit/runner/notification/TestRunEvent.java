package org.junit.runner.notification;

/**
 * An ignored test, invalid assumption, or test failure to notify the user about
 */
public abstract class TestRunEvent {
	/**
	 * @return a user-understandable label for the test
	 */
	public abstract String getTestHeader();

	/**
	 * Convenience method
	 * @return the printed form of the exception, if any
	 */
	public abstract String getTrace();
}
