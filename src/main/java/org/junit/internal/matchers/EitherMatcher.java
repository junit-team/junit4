package org.junit.internal.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.allOf;

public class EitherMatcher<T> extends BaseMatcher<T> {

	private final Matcher<? extends T> fMatcher;

	public EitherMatcher(Matcher<? extends T> matcher) {
		fMatcher = matcher;
	}

	public boolean matches(Object item) {
		return fMatcher.matches(item);
	}

	public void describeTo(Description description) {
		description.appendDescriptionOf(fMatcher);
	}

	@SuppressWarnings("unchecked")
	public EitherMatcher<T> or(Matcher<? extends T> matcher) {
		return new EitherMatcher<T>(anyOf(matcher, fMatcher));
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public EitherMatcher<T> and(Matcher<? extends T> matcher) {
		return new EitherMatcher<T>(allOf(matcher, fMatcher));
	}
	
}
