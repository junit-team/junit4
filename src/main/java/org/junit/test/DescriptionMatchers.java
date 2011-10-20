package org.junit.test;

import org.hamcrest.Matcher;
import org.junit.runner.Description;
import org.junit.test.internal.HasDisplayName;

/**
 * A collection of hamcrest matchers for the {@link Description}.
 */
public class DescriptionMatchers {
	/**
	 * Evaluates to true if the {@link Description} has the specified display
	 * name.
	 * 
	 * @param name
	 *            the expected display name.
	 */
	public static Matcher<Description> hasDisplayName(String name) {
		return new HasDisplayName(name);
	}

	/**
	 * Evaluates to true if the specified matcher returns true for the
	 * {@link Description}'s display name.
	 */
	public static Matcher<Description> hasDisplayName(Matcher<String> matcher) {
		return new HasDisplayName(matcher);
	}
}
