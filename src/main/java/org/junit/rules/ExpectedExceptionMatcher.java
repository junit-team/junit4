package org.junit.rules;

import static org.hamcrest.CoreMatchers.allOf;

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
	private Matcher<?> fCompositeMatcher;
	
	void and(Matcher<?> matcher) {
		fMatchers.add(matcher);
		fCompositeMatcher= createCompositeMatcher();
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

	public void describeTo(Description description) {
		fCompositeMatcher.describeTo(description);
	}

	private Matcher<?> createCompositeMatcher() {
		if (fMatchers.size() == 1) {
			return fMatchers.get(0);
		}
		return allOf(castedMatchers());
	}

	// Should be able to remove this suppression in some brave new hamcrest
	// world.
	@SuppressWarnings("unchecked")
	private List<Matcher<? super Object>> castedMatchers() {
		List<Matcher<? super Object>> castedMatchers = new LinkedList<Matcher<? super Object>>();
		for (Matcher<?> matcher : fMatchers) {
			castedMatchers.add((Matcher<? super Object>) matcher);
		}
		return castedMatchers;
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
