package org.junit.matchers;

import org.hamcrest.Matcher;

public class JUnitMatchers {
	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItem(T element) {
		return IsCollectionContaining.hasItem(element);
	}

	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItem(org.hamcrest.Matcher<? extends T> elementMatcher) {
		return IsCollectionContaining.hasItem(elementMatcher);
	}

	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItems(org.hamcrest.Matcher<? extends T>... elementMatchers) {
		return IsCollectionContaining.hasItems(elementMatchers);
	}

	public static <T> org.hamcrest.Matcher<java.lang.Iterable<T>> hasItems(T... elements) {
		return IsCollectionContaining.hasItems(elements);
	}

	public static org.hamcrest.Matcher<java.lang.String> containsString(java.lang.String substring) {
		return StringContains.containsString(substring);
	}

	public static <T> Matcher<Iterable<T>> each(final Matcher<T> individual) {
		return Each.each(individual);
	}
	
	public static <T> CombinableMatcher<T> both(Matcher<T> matcher) {
		return new CombinableMatcher<T>(matcher);
	}
	
	public static <T> CombinableMatcher<T> either(Matcher<T> matcher) {
		return new CombinableMatcher<T>(matcher);
	}	
}
