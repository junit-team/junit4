package org.junit.experimental.results;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ResultMatchers {
	public static Matcher<PrintableResult> isSuccessful() {
		return failureCountIs(0);
	}

	public static Matcher<PrintableResult> failureCountIs(final int count) {
		return new BaseMatcher<PrintableResult>() {
			public boolean matches(Object item) {
				return ((PrintableResult) item).getFailures().size() == count;
			}

			public void describeTo(Description description) {
				description.appendText("has " + count + " failures");
			}
		};
	}
	
	// TODO: (Dec 7, 2007 10:15:15 AM) fix type

	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	public static Matcher<PrintableResult> hasFailureContaining(final String string) {
		return new BaseMatcher<PrintableResult>() {
			public boolean matches(Object item) {
				return item.toString().contains(string);
			}

			public void describeTo(Description description) {
				// TODO: (Dec 7, 2007 10:14:35 AM) not right
				description.appendText("has single failure containing " + string);
			}
		};
	}
}
