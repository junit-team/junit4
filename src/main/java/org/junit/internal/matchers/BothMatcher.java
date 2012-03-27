package org.junit.internal.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;

/**
 * Used to combine hamcrest matchers that must both pass.
 * @see org.junit.matchers.JUnitMatchers#both(Matcher) 
 */
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
	
	/**
	 * @deprecated doesn't make sense to use an or combination in an operation
	 * where both matchers must pass. Use {@link EitherMatcher#or(Matcher)} 
	 * instead.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public BothMatcher<T> or(Matcher<? extends T> matcher) {
		return new BothMatcher<T>(anyOf(matcher, fMatcher));
	}

}
