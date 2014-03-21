package org.junit.tests.experimental.rules;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

class EventCollector extends RunListener {
    static Matcher<EventCollector> everyTestRunSuccessful() {
        return allOf(hasNoFailure(), hasNoAssumptionFailure());
    }

    private static Matcher<EventCollector> hasNumberOfFailures(
            final int numberOfFailures) {
        return new TypeSafeMatcher<EventCollector>() {
            @Override
            public boolean matchesSafely(EventCollector item) {
                return item.failures.size() == numberOfFailures;
            }

            public void describeTo(org.hamcrest.Description description) {
                description.appendText("has ");
                description.appendValue(numberOfFailures);
                description.appendText(" failures");
            }

            @Override
            protected void describeMismatchSafely(EventCollector item,
                    org.hamcrest.Description description) {
                description.appendValue(item.failures.size());
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
        return new TypeSafeMatcher<EventCollector>() {
            @Override
            public boolean matchesSafely(EventCollector item) {
                return item.assumptionFailures.size() == numberOfFailures;
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
        return new TypeSafeMatcher<EventCollector>() {
            @Override
            public boolean matchesSafely(EventCollector item) {
                return hasSingleFailure().matches(item)
                        && messageMatcher.matches(item.failures.get(0)
                        .getMessage());
            }

            public void describeTo(org.hamcrest.Description description) {
                description.appendText("has single failure with message ");
                messageMatcher.describeTo(description);
            }

            @Override
            protected void describeMismatchSafely(EventCollector item,
                    org.hamcrest.Description description) {
                description.appendText("was ");
                hasSingleFailure().describeMismatch(item, description);
                description.appendText(": ");
                boolean first= true;
                for (Failure f : item.failures) {
                    if (!first) {
                        description.appendText(" ,");
                    }
                    description.appendText("'");
                    description.appendText(f.getMessage());
                    description.appendText("'");
                    first= false;
                }
            }
        };
    }

    static Matcher<EventCollector> failureIs(final Matcher<? super Throwable> exceptionMatcher) {
        return new TypeSafeMatcher<EventCollector>() {
            @Override
            public boolean matchesSafely(EventCollector item) {
                for (Failure f : item.failures) {
                    return exceptionMatcher.matches(f.getException());
                }
                return false;
            }

            public void describeTo(org.hamcrest.Description description) {
                description.appendText("failure is ");
                exceptionMatcher.describeTo(description);
            }
        };
    }

    private final List<Description> testRunsStarted = new ArrayList<Description>();

    private final List<Result> testRunsFinished = new ArrayList<Result>();

    private final List<Description> testsStarted = new ArrayList<Description>();

    private final List<Description> testsFinished = new ArrayList<Description>();

    private final List<Failure> failures = new ArrayList<Failure>();

    private final List<Failure> assumptionFailures = new ArrayList<Failure>();

    private final List<Description> testsIgnored = new ArrayList<Description>();

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
        testsIgnored.add(description);
    }

    @Override
    public String toString() {
        return testRunsStarted.size() + " test runs started, "
            + testRunsFinished.size() + " test runs finished, "
            + testsStarted.size() + " tests started, "
            + testsFinished.size() + " tests finished, "
            + failures.size() + " failures, "
            + assumptionFailures.size() + " assumption failures, "
            + testsIgnored.size() + " tests ignored";
    }
}
