package org.junit.matchers;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.CombinableMatcher;

/**
 * Convenience import class: these are useful matchers for use with the assertThat method, but they are
 * not currently included in the basic CoreMatchers class from hamcrest.
 */
public class JUnitMatchers {
	// TODO (May 27, 2009 11:46:27 AM): deprecate all?
	/**
	 * @param element
	 * @return A matcher matching any collection containing element
	 * @deprecated Use org.hamcrest.CoreMatchers.hasItem
	 */
	@Deprecated
	public static <T> Matcher<Iterable<? super T>> hasItem(T element) {
		return CoreMatchers.<T>hasItem(element);
	}

	/**
	 * @param elementMatcher
	 * @return A matcher matching any collection containing an element matching elementMatcher
	 * @deprecated Use org.hamcrest.CoreMatchers.hasItem
	 */
	@Deprecated
    public static <T> Matcher<Iterable<? super T>> hasItem(Matcher<? super T> elementMatcher) {
		return CoreMatchers.<T>hasItem(elementMatcher);
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
	public static <T> Matcher<Iterable<T>> each(final Matcher<T> elementMatcher) {
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
	 */
	public static <T> CombinableMatcher<T> both(Matcher<T> matcher) {
		return CoreMatchers.both(matcher);
	}
	
	/**
	 * This is useful for fluently combining matchers where either may pass, for example:
	 * <pre>
	 *   assertThat(string, either(containsString("a")).or(containsString("b")));
	 * </pre>
	 * 
	 * If you want to say either(is(3)).or(is(4)), and are prevented,
	 * please see isOneOf(...) below.
	 */
	public static <T> CombinableMatcher<T> either(Matcher<T> matcher) {
		return CoreMatchers.either(matcher);
	}
	
	/**
	 * This is sugar for the situation where 
	 * For example:
	 * <pre>
	 *   assertThat(string, eitherIs("a").or(is("b")));
	 * </pre>
	 */
	public static <T> Matcher<T> isOneOf(T... objects) {
		List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>();
		for (T each : objects) {
			matchers.add(is(each));
		}
		return anyOf(matchers);
	}
	
	/**
	 * Loosens type parameter, in order to use a Matcher 
	 * in a place where Java doesn't want to typecheck:
	 *
	 * Goofy example:
	 * <pre>
	 *   assertThat(3, matches(containsString("a")));
	 * </pre>
	 * 
	 * Real example:
	 * <pre>
	 *   assertThat(3, either(matches(is(String.class))).or(
	 *		                  matches(is(Integer.class))));
     *
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Matcher<T> matches(Matcher<?> matcher) {
		return (Matcher<T>)matcher;
	}
}
