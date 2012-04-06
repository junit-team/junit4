package org.junit.test;

import org.hamcrest.Matcher;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.test.internal.HasDescription;
import org.junit.test.internal.HasMessage;

/**
 * A collection of hamcrest matchers for the {@link Failure}.
 */
public class FailureMatchers {
	/**
	 * Evaluates to true if the {@link Failure} has the specified message.
	 * 
	 * @param message
	 *            the expected message.
	 */
	public static Matcher<Failure> hasMessage(String message) {
		return new HasMessage(message);
	}

	/**
	 * Evaluates to true if the specified matcher returns true for the
	 * {@link Failure}'s message.
	 */
	public static Matcher<Failure> hasMessage(Matcher<String> matcher) {
		return new HasMessage(matcher);
	}

	/**
	 * Evaluates to true if the {@link Failure} has the specified description.
	 * 
	 * @param description
	 *            the expected description.
	 */
	public static Matcher<Failure> hasDescription(Description description) {
		return new HasDescription(description);
	}

	/**
	 * Evaluates to true if the specified matcher returns true for the
	 * {@link Failure}'s description.
	 */
	public static Matcher<Failure> hasDescription(Matcher<Description> matcher) {
		return new HasDescription(matcher);
	}
}
