package org.junit.internal.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;

public class BothMatcher<T> extends BaseMatcher<T> {

	private final Matcher<? extends T> fMatcher;
	
	public BothMatcher(Matcher<? extends T> matcher) {
		fMatcher = matcher;
	}

	public boolean matches(Object item) {
		return fMatcher.matches(item);
	}

	public void describeTo(Description description) {
		description.appendDescriptionOf(fMatcher);
	}
	
	@SuppressWarnings("unchecked")
	public BothMatcher<T> and(Matcher<? extends T> matcher) {
		return new BothMatcher<T>(allOf(matcher, fMatcher));
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public BothMatcher<T> or(Matcher<? extends T> matcher) {
		return new BothMatcher<T>(anyOf(matcher, fMatcher));
	}

}
