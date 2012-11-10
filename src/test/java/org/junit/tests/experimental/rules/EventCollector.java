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
                return item.fFailures.size() == numberOfFailures;
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
        return new TypeSafeMatcher<EventCollector>() {
            @Override
            public boolean matchesSafely(EventCollector item) {
                return item.fAssumptionFailures.size() == numberOfFailures;
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
                        && messageMatcher.matches(item.fFailures.get(0)
                        .getMessage());
            }

            public void describeTo(org.hamcrest.Description description) {
                description.appendText("has single failure with message ");
                messageMatcher.describeTo(description);
            }
        };
    }

    static Matcher<EventCollector> failureIs(final Matcher<? super Throwable> exceptionMatcher) {
        return new TypeSafeMatcher<EventCollector>() {
            @Override
            public boolean matchesSafely(EventCollector item) {
                for (Failure f : item.fFailures) {
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

    private final List<Description> fTestRunsStarted = new ArrayList<Description>();

    private final List<Result> fTestRunsFinished = new ArrayList<Result>();

    private final List<Description> fTestsStarted = new ArrayList<Description>();

    private final List<Description> fTestsFinished = new ArrayList<Description>();

    private final List<Failure> fFailures = new ArrayList<Failure>();

    private final List<Failure> fAssumptionFailures = new ArrayList<Failure>();

    private final List<Description> fTestsIgnored = new ArrayList<Description>();

    @Override
    public void testRunStarted(Description description) throws Exception {
        fTestRunsStarted.add(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        fTestRunsFinished.add(result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        fTestsStarted.add(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        fTestsFinished.add(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        fFailures.add(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        fAssumptionFailures.add(failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        fTestsIgnored.add(description);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fTestRunsStarted.size());
        sb.append(" test runs started, ");
        sb.append(fTestRunsFinished.size());
        sb.append(" test runs finished, ");
        sb.append(fTestsStarted.size());
        sb.append(" tests started, ");
        sb.append(fTestsFinished.size());
        sb.append(" tests finished, ");
        sb.append(fFailures.size());
        sb.append(" failures, ");
        sb.append(fAssumptionFailures.size());
        sb.append(" assumption failures, ");
        sb.append(fTestsIgnored.size());
        sb.append(" tests ignored");

        return sb.toString();
    }
}
