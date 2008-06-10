package org.junit.matchers;

import org.hamcrest.Matcher;
import org.junit.internal.matchers.CombinableMatcher;
import org.junit.internal.matchers.Each;
import org.junit.internal.matchers.IsCollectionContaining;
import org.junit.internal.matchers.StringContains;

/**
 * Convenience import class: these are useful matchers for use with the assertThat method, but they are
 * not currently included in the basic CoreMatchers class from hamcrest.
 */
public class JUnitMatchers {
	/**
	 * @param element
	 * @return A matcher matching any collection containing element
	 */
	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItem(T element) {
		return IsCollectionContaining.hasItem(element);
	}

	/**
	 * @param elementMatcher
	 * @return A matcher matching any collection containing an element matching elementMatcher
	 */
	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItem(org.hamcrest.Matcher<? extends T> elementMatcher) {
		return IsCollectionContaining.hasItem(elementMatcher);
	}

	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItems(org.hamcrest.Matcher<? extends T>... elementMatchers) {
		return IsCollectionContaining.hasItems(elementMatchers);
	}

	/**
	 * @param element
	 * @return A matcher matching any collection containing every element in elements
	 */
	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItems(T... elements) {
		return IsCollectionContaining.hasItems(elements);
	}

	/**
	 * @param elementMatcher
	 * @return A matcher matching any collection in which every element matches elementMatcher
	 */
	public static <T> Matcher<Iterable<T>> everyItem(final Matcher<T> elementMatcher) {
		return Each.each(elementMatcher);
	}

	public static org.hamcrest.Matcher<java.lang.String> containsString(java.lang.String substring) {
		return StringContains.containsString(substring);
	}
	
	public static <T> CombinableMatcher<T> both(Matcher<T> matcher) {
		return new CombinableMatcher<T>(matcher);
	}
	
	public static <T> CombinableMatcher<T> either(Matcher<T> matcher) {
		return new CombinableMatcher<T>(matcher);
	}	
}
