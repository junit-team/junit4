package org.junit.test.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class HasDescription extends BaseMatcher<Failure> {
	private final Matcher<Description> fMatcher;

	public HasDescription(Description description) {
		this(is(equalTo(description)));
	}

	public HasDescription(Matcher<Description> matcher) {
		fMatcher= matcher;
	}

	public boolean matches(Object item) {
		Failure failure= (Failure) item;
		return (failure != null) && fMatcher.matches(failure.getDescription());
	}

	public void describeTo(org.hamcrest.Description description) {
		description.appendText("description ");
		description.appendDescriptionOf(fMatcher);
	}
}
