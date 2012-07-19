package org.junit.rules;

import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.matchers.JUnitMatchers.withStacktrace;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Special matcher used by {@link ExpectedException}.
 */
class ExpectedExceptionMatcher extends TypeSafeMatcher<Throwable> {
	
	private final List<Matcher<?>> fMatchers = new ArrayList<Matcher<?>>();
	private Matcher<Throwable> fCompositeMatcher;
	
	void and(Matcher<?> matcher) {
		fMatchers.add(matcher);
		fCompositeMatcher= withStacktrace(createCompositeMatcher());
	}

	void andHasMessage(Matcher<String> matcher) {
		and(hasMessage(matcher));
	}

	void andHasCause(Matcher<? extends Throwable> causeMatcher) {
		and(hasCause(causeMatcher));
	}

	boolean expectsThrowable() {
		return !fMatchers.isEmpty();
	}

	@Override
	protected boolean matchesSafely(Throwable item) {
		return fCompositeMatcher.matches(item);
	}
	
	@Override
	protected void describeMismatchSafely(Throwable item,
			Description mismatchDescription) {
		fCompositeMatcher.describeMismatch(item, mismatchDescription);
	}

	public void describeTo(Description description) {
		fCompositeMatcher.describeTo(description);
	}

	private Matcher<Throwable> createCompositeMatcher() {
		if (fMatchers.size() == 1) {
			return cast(fMatchers.get(0));
		}
		return allOf(castedMatchers());
	}

	private List<Matcher<? super Throwable>> castedMatchers() {
		List<Matcher<? super Throwable>> castedMatchers = new LinkedList<Matcher<? super Throwable>>();
		for (Matcher<?> matcher : fMatchers) {
			castedMatchers.add(cast(matcher));
		}
		return castedMatchers;
	}

	// Should be able to remove this suppression in some brave new hamcrest
	// world.
	@SuppressWarnings("unchecked")
	private Matcher<Throwable> cast(Matcher<?> singleMatcher) {
		return (Matcher<Throwable>) singleMatcher;
	}

	private Matcher<Throwable> hasMessage(final Matcher<String> matcher) {
		return new TypeSafeMatcher<Throwable>() {
			public void describeTo(Description description) {
				description.appendText("exception with message ");
				description.appendDescriptionOf(matcher);
			}

			@Override
			public boolean matchesSafely(Throwable item) {
				return matcher.matches(item.getMessage());
			}
		};
	}

	private Matcher<Throwable> hasCause(final Matcher<? extends Throwable> causeMatcher) {
		return new TypeSafeMatcher<Throwable>() {
			public void describeTo(Description description) {
				description.appendText("exception with cause ");
				description.appendDescriptionOf(causeMatcher);
			}

			@Override
			public boolean matchesSafely(Throwable item) {
				return causeMatcher.matches(item.getCause());
			}
		};
	}
}
