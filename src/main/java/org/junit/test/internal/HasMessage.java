package org.junit.test.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.runner.notification.Failure;

public class HasMessage extends BaseMatcher<Failure> {
	private final Matcher<String> fMatcher;

	public HasMessage(String name) {
		this(is(equalTo(name)));
	}

	public HasMessage(Matcher<String> matcher) {
		fMatcher= matcher;
	}

	public boolean matches(Object item) {
		Failure failure= (Failure) item;
		return (failure != null) && fMatcher.matches(failure.getMessage());
	}

	public void describeTo(Description description) {
		description.appendText("message ");
		description.appendDescriptionOf(fMatcher);
	}
}
