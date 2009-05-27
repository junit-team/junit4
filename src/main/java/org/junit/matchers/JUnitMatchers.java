package org.junit.matchers;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.CombinableMatcher;

/**
 * Convenience import class: these are useful matchers for use with the assertThat method, but they are
 * not currently included in the basic CoreMatchers class from hamcrest.
 * @deprecated as of JUnit 4.7, all of these matchers are found in the org.hamcrest.CoreMatchers
 *             class instead.
 */
@Deprecated
public class JUnitMatchers {
	// TODO (May 27, 2009 11:46:27 AM): deprecate all?
	/**
	 * @param element
	 * @return A matcher matching any collection containing element
	 * @deprecated Use org.hamcrest.CoreMatchers.hasItem
	 */
	@Deprecated
	public static <T> Matcher<Iterable<? super T>> hasItem(T element) {
		return CoreMatchers.hasItem(element);
	}

	/**
	 * @param elementMatcher
	 * @return A matcher matching any collection containing an element matching elementMatcher
	 * @deprecated Use org.hamcrest.CoreMatchers.hasItem
	 */
	@Deprecated
    public static <T> Matcher<Iterable<? super T>> hasItem(Matcher<? super T> elementMatcher) {
		return CoreMatchers.hasItem(elementMatcher);
	}

	/**
	 * @param elements
	 * @return A matcher matching any collection containing every element in elements
	 * @deprecated Use org.hamcrest.CoreMatchers.hasItems
	 */
	@Deprecated
	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItems(T... elements) {
		return CoreMatchers.hasItems(elements);
	}

	/**
	 * @param elementMatchers
	 * @return A matcher matching any collection containing at least one element that matches 
	 *         each matcher in elementMatcher (this may be one element matching all matchers,
	 *         or different elements matching each matcher)
	 * @deprecated Use org.hamcrest.CoreMatchers.hasItems
	 */
	@Deprecated
	public static <T> Matcher<Iterable<T>> hasItems(Matcher<? super T>... elementMatchers) {
		return CoreMatchers.hasItems(elementMatchers);
	}

	/**
	 * @param elementMatcher
	 * @return A matcher matching any collection in which every element matches elementMatcher
	 * @deprecated use CoreMatchers.everyItem directly
	 */
	@Deprecated
	public static <T> Matcher<Iterable<T>> everyItem(final Matcher<T> elementMatcher) {
		return CoreMatchers.everyItem(elementMatcher);
	}

	/**
	 * @param substring
	 * @return a matcher matching any string that contains substring
	 * @deprecated Use org.hamcrest.CoreMatchers.containsString
	 */
	@Deprecated
	public static org.hamcrest.Matcher<java.lang.String> containsString(java.lang.String substring) {
		return CoreMatchers.containsString(substring);
	}
	
	/**
	 * This is useful for fluently combining matchers that must both pass.  For example:
	 * <pre>
	 *   assertThat(string, both(containsString("a")).and(containsString("b")));
	 * </pre>
	 * @deprecated Use org.hamcrest.CoreMatchers.both
	 */
	@Deprecated
	public static <T> CombinableMatcher<T> both(Matcher<? super T> matcher) {
		return CoreMatchers.both(matcher);
	}
	
	/**
	 * This is useful for fluently combining matchers where either may pass, for example:
	 * <pre>
	 *   assertThat(string, both(containsString("a")).and(containsString("b")));
	 * </pre>
	 * @deprecated Use org.hamcrest.CoreMatchers.either
	 */
	@Deprecated
	public static <T> CombinableMatcher<T> either(Matcher<? super T> matcher) {
		return CoreMatchers.either(matcher);
	}	
}
