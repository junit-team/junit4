package org.junit.test.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.runner.Description;

public class HasDisplayName extends BaseMatcher<Description> {
	private final Matcher<String> fMatcher;

	public HasDisplayName(String name) {
		this(is(equalTo(name)));
	}

	public HasDisplayName(Matcher<String> matcher) {
		this.fMatcher= matcher;
	}

	public boolean matches(Object item) {
		Description description= (Description) item;
		return (description != null)
				&& fMatcher.matches(description.getDisplayName());
	}

	public void describeTo(org.hamcrest.Description description) {
		description.appendText("display name ");
		description.appendDescriptionOf(fMatcher);
	}
}
