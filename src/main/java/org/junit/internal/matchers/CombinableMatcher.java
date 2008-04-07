package org.junit.internal.matchers;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CombinableMatcher<T> extends BaseMatcher<T> {

	private final Matcher<? extends T> fMatcher;

	public CombinableMatcher(Matcher<? extends T> matcher) {
		fMatcher= matcher;
	}

	public boolean matches(Object item) {
		return fMatcher.matches(item);
	}

	public void describeTo(Description description) {
		description.appendDescriptionOf(fMatcher);
	}
	
	@SuppressWarnings("unchecked")
	public CombinableMatcher<T> and(Matcher<? extends T> matcher) {
		return new CombinableMatcher<T>(allOf(matcher, fMatcher));
	}

	@SuppressWarnings("unchecked")
	public CombinableMatcher<T> or(Matcher<? extends T> matcher) {
		return new CombinableMatcher<T>(anyOf(matcher, fMatcher));
	}
}