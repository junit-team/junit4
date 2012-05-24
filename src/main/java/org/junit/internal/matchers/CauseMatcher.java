package org.junit.internal.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ThrowableMatcher extends TypeSafeMatcher<Throwable> {
	private final Matcher<? super Throwable> fCauseMatcher;

	public ThrowableMatcher(final Matcher<? super Throwable> causeMatcher) {
		fCauseMatcher= causeMatcher;
	}
	
	public void describeTo(Description description) {
		description.appendText("caused by ");
		fCauseMatcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(Throwable item) {
		return fCauseMatcher.matches(item.getCause());
	}
}
