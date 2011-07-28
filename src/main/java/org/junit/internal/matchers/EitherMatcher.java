package org.junit.internal.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Used to combine hamcrest matchers that either may pass.
 * @see org.junit.matchers.JUnitMatchers#either(Matcher)
 */
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
	
	/**
	 * @deprecated doesn't make sense to use an and combination in an operation
	 * where either matchers may pass. Use {@link BothMatcher#and(Matcher)}}
	 * instead. 
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public EitherMatcher<T> and(Matcher<? extends T> matcher) {
		return new EitherMatcher<T>(allOf(matcher, fMatcher));
	}
	
}
