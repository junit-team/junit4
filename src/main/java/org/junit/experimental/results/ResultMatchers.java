package org.junit.experimental.results;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Matchers on a PrintableResult, to enable JUnit self-tests.
 * For example:
 * 
 * <pre>
 * 		assertThat(testResult(HasExpectedException.class), isSuccessful());
 * </pre>
 */
public class ResultMatchers {
	/**
	 * Matches if the tests are all successful
	 */
	public static Matcher<PrintableResult> isSuccessful() {
		return failureCountIs(0);
	}

	/**
	 * Matches if there are {@code count} failures
	 */
	public static Matcher<PrintableResult> failureCountIs(final int count) {
		return new TypeSafeMatcher<PrintableResult>() {
			public void describeTo(Description description) {
				description.appendText("has " + count + " failures");
			}

			@Override
			public boolean matchesSafely(PrintableResult item) {
				return item.failureCount() == count;
			}
		};
	}
	
	/**
	 * Matches if the result has exactly one failure, and it contains {@code string}
	 */
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

	/**
	 * Matches if the result has one or more failures, and at least one of them
	 * contains {@code string}
	 */
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
	
	/**
	 * Matches if the failure matches the given {@code failureMatcher}
	 */
	public static Matcher<Result> failureIs(final Matcher<? super Throwable> failureMatcher) {
		return new TypeSafeMatcher<Result>() {
			public void describeTo(Description description) {
				description.appendText("failure is ");
				failureMatcher.describeTo(description);
			}
	
			@Override
			public boolean matchesSafely(Result item) {
				for(Failure f: item.getFailures())
					return failureMatcher.matches(f.getException());
				return false;
			}
		};
	}

	/**
	 * Matches if the cause of the exception matches the given {@code causeMatcher}
	 */
	public static Matcher<Throwable> causedBy(final Matcher<? super Throwable> causeMatcher) {
		return new TypeSafeMatcher<Throwable>() {
			public void describeTo(Description description) {
				description.appendText("caused by ");
				causeMatcher.describeTo(description);
			}
	
			@Override
			public boolean matchesSafely(Throwable item) {
				return causeMatcher.matches(item.getCause());
			}
		};
	}
}
