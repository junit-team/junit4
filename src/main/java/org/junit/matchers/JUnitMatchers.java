package org.junit.matchers;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.CombinableMatcher;

/**
 * Convenience import class: these are useful matchers for use with the assertThat method, but they are
 * not currently included in the basic CoreMatchers class from hamcrest.
 */
public class JUnitMatchers {
	/**
	 * @param element
	 * @return A matcher matching any collection containing element
	 * 
	 * @deprecated Please use {@link CoreMatchers#hasItem(Object)} instead.
	 */
	@Deprecated
	public static <T> Matcher<Iterable<? super T>> hasItem(T element) {
		return CoreMatchers.hasItem(element);
	}

	/**
	 * @param elementMatcher
	 * @return A matcher matching any collection containing an element matching elementMatcher
	 * 
	 * @deprecated Please use {@link CoreMatchers#hasItem(Matcher)} instead.
	 */
	@Deprecated
	public static <T> Matcher<Iterable<? super T>> hasItem(Matcher<? super T> elementMatcher) {
		return CoreMatchers.hasItem(elementMatcher);
	}

	/**
	 * @param elements
	 * @return A matcher matching any collection containing every element in elements
	 * 
	 * @deprecated Please use {@link CoreMatchers#hasItems(Object...)} instead.
	 */
	@Deprecated
	public static <T> Matcher<Iterable<T>> hasItems(T... elements) {
		return CoreMatchers.hasItems(elements);
	}

	/**
	 * @param elementMatchers
	 * @return A matcher matching any collection containing at least one element that matches 
	 *         each matcher in elementMatcher (this may be one element matching all matchers,
	 *         or different elements matching each matcher)
	 * 
	 * @deprecated Please use {@link CoreMatchers#hasItems(Matcher...)} instead.
	 */
	@Deprecated
	public static <T> Matcher<Iterable<T>> hasItems(Matcher<? super T>... elementMatchers) {
		return CoreMatchers.hasItems(elementMatchers);
	}

	/**
	 * @param elementMatcher
	 * @return A matcher matching any collection in which every element matches elementMatcher
	 * 
	 * @deprecated Please use {@link CoreMatchers#everyItem(Matcher)} instead.
	 */
	@Deprecated
	public static <T> Matcher<Iterable<T>> everyItem(final Matcher<T> elementMatcher) {
		return CoreMatchers.everyItem(elementMatcher);
	}

	/**
	 * @param substring
	 * @return a matcher matching any string that contains substring
	 * 
	 * @deprecated Please use {@link CoreMatchers#containsString(String)} instead.
	 */
	@Deprecated
	public static Matcher<java.lang.String> containsString(java.lang.String substring) {
		return CoreMatchers.containsString(substring);
	}
	
	/**
	 * This is useful for fluently combining matchers that must both pass.  For example:
	 * <pre>
	 *   assertThat(string, both(containsString("a")).and(containsString("b")));
	 * </pre>
	 * 
	 * @deprecated Please use {@link CoreMatchers#both(Matcher)} instead.
	 */
	@Deprecated
	public static <T> CombinableMatcher<T> both(Matcher<? super T> matcher) {
		return CoreMatchers.both(matcher);
	}
	
	/**
	 * This is useful for fluently combining matchers where either may pass, for example:
	 * <pre>
	 *   assertThat(string, either(containsString("a")).or(containsString("b")));
	 * </pre>
	 * 
	 * @deprecated Please use {@link CoreMatchers#either(Matcher)} instead.
	 */
	@Deprecated
	public static <T> CombinableMatcher<T> either(Matcher<? super T> matcher) {
		return CoreMatchers.either(matcher);
	}	
}
