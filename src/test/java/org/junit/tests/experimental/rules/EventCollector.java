package org.junit.tests.experimental.rules;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.matchers.JUnitMatchers.both;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

class EventCollector extends RunListener {
	private final List<Description> testRunsStarted= new ArrayList<Description>();

	private final List<Result> testRunsFinished= new ArrayList<Result>();

	private final List<Description> testsStarted= new ArrayList<Description>();

	private final List<Description> testsFinished= new ArrayList<Description>();

	private final List<Failure> failures= new ArrayList<Failure>();

	private final List<Failure> assumptionFailures= new ArrayList<Failure>();

	private final List<Description> testIgnored= new ArrayList<Description>();

	@Override
	public void testRunStarted(Description description) throws Exception {
		testRunsStarted.add(description);
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		testRunsFinished.add(result);
	}

	@Override
	public void testStarted(Description description) throws Exception {
		testsStarted.add(description);
	}

	@Override
	public void testFinished(Description description) throws Exception {
		testsFinished.add(description);
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		failures.add(failure);
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		assumptionFailures.add(failure);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		testIgnored.add(description);
	}

	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder();
		sb.append(testRunsStarted.size());
		sb.append(" test runs started, ");
		sb.append(testRunsFinished.size());
		sb.append(" test runs finished, ");
		sb.append(testsStarted.size());
		sb.append(" tests started, ");
		sb.append(testsFinished.size());
		sb.append(" tests finished, ");
		sb.append(failures.size());
		sb.append(" failures, ");
		sb.append(assumptionFailures.size());
		sb.append(" assumption failures, ");
		sb.append(testIgnored.size());
		sb.append(" tests ignored");

		return sb.toString();
	}

	static Matcher<EventCollector> everyTestRunSuccessful() {
		return both(hasNoFailure()).and(hasNoAssumptionFailure());
	}

	private static Matcher<EventCollector> hasNumberOfFailures(
			final int numberOfFailures) {
		return new BaseMatcher<EventCollector>() {
			public boolean matches(Object item) {
				return ((EventCollector) item).failures.size() == numberOfFailures;
			}

			public void describeTo(org.hamcrest.Description description) {
				description.appendText("has ");
				description.appendValue(numberOfFailures);
				description.appendText(" failures");
			}
		};
	}

	static Matcher<EventCollector> hasSingleFailure() {
		return hasNumberOfFailures(1);
	}

	static Matcher<EventCollector> hasNoFailure() {
		return hasNumberOfFailures(0);
	}

	private static Matcher<EventCollector> hasNumberOfAssumptionFailures(
			final int numberOfFailures) {
		return new BaseMatcher<EventCollector>() {
			public boolean matches(Object item) {
				return ((EventCollector) item).assumptionFailures.size() == numberOfFailures;
			}

			public void describeTo(org.hamcrest.Description description) {
				description.appendText("has ");
				description.appendValue(numberOfFailures);
				description.appendText(" assumption failures");
			}
		};
	}

	static Matcher<EventCollector> hasSingleAssumptionFailure() {
		return hasNumberOfAssumptionFailures(1);
	}

	static Matcher<EventCollector> hasNoAssumptionFailure() {
		return hasNumberOfAssumptionFailures(0);
	}

	static Matcher<EventCollector> hasSingleFailureWithMessage(String message) {
		return hasSingleFailureWithMessage(equalTo(message));
	}

	static Matcher<EventCollector> hasSingleFailureWithMessage(
			final Matcher<String> messageMatcher) {
		return new BaseMatcher<EventCollector>() {
			public boolean matches(Object item) {
				return hasSingleFailure().matches(item)
						&& messageMatcher
								.matches(((EventCollector) item).failures
										.get(0).getMessage());
			}

			public void describeTo(org.hamcrest.Description description) {
				description.appendText("has single failure with message ");
				messageMatcher.describeTo(description);
			}
		};
	}
}
