package org.junit.experimental.results;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

public class ResultMatchers {
	public static Matcher<PrintableResult> isSuccessful() {
		return failureCountIs(0);
	}

	public static Matcher<PrintableResult> failureCountIs(final int count) {
		return new TypeSafeMatcher<PrintableResult>() {
			public void describeTo(Description description) {
				description.appendText("has " + count + " failures");
			}

			@Override
			public boolean matchesSafely(PrintableResult item) {
				return item.getFailures().size() == count;
			}
		};
	}
	
	public static Matcher<Object> hasSingleFailureContaining(final String string) {
		return new BaseMatcher<Object>() {
			public boolean matches(Object item) {
				return item.toString().contains(string) && failureCountIs(1).matches(item);
			}

			public void describeTo(Description description) {
				description.appendText("has single failure containing " + string);
			}
		};
	}
	
	public static Matcher<PrintableResult> hasFailureContaining(final String string) {
		return new BaseMatcher<PrintableResult>() {
			public boolean matches(Object item) {
				return item.toString().contains(string);
			}

			public void describeTo(Description description) {
				description.appendText("has failure containing " + string);
			}
		};
	}
}
