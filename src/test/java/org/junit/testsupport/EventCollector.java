package org.junit.testsupport;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * A {@link org.junit.runner.notification.RunListener} that collects all events.
 */
public class EventCollector extends RunListener {

    private final List<Description> testRunsStarted = synchronizedList(new ArrayList<Description>());

    private final List<Result> testRunsFinished = synchronizedList(new ArrayList<Result>());

    private final List<Description> testSuitesStarted = synchronizedList(new ArrayList<Description>());

    private final List<Description> testSuitesFinished = synchronizedList(new ArrayList<Description>());

    private final List<Description> testsStarted = synchronizedList(new ArrayList<Description>());

    private final List<Description> testsFinished = synchronizedList(new ArrayList<Description>());

    private final List<Failure> failures = synchronizedList(new ArrayList<Failure>());

    private final List<Failure> assumptionFailures = synchronizedList(new ArrayList<Failure>());

    private final List<Description> testsIgnored = synchronizedList(new ArrayList<Description>());

    @Override
    public void testRunStarted(Description description) {
        testRunsStarted.add(description);
    }

    @Override
    public void testRunFinished(Result result) {
        testRunsFinished.add(result);
    }

    @Override
    public void testSuiteStarted(Description description) {
        testSuitesStarted.add(description);
    }

    @Override
    public void testSuiteFinished(Description description) {
        testSuitesFinished.add(description);
    }

    @Override
    public void testStarted(Description description) {
        testsStarted.add(description);
    }

    @Override
    public void testFinished(Description description) {
        testsFinished.add(description);
    }

    @Override
    public void testFailure(Failure failure) {
        failures.add(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        assumptionFailures.add(failure);
    }

    @Override
    public void testIgnored(Description description) {
        testsIgnored.add(description);
    }

    public List<Description> getTestRunsStarted() {
        return unmodifiableList(testRunsStarted);
    }

    public List<Result> getTestRunsFinished() {
        return unmodifiableList(testRunsFinished);
    }

    public List<Description> getTestSuitesStarted() {
        return unmodifiableList(testSuitesStarted);
    }

    public List<Description> getTestSuitesFinished() {
        return unmodifiableList(testSuitesFinished);
    }

    public List<Description> getTestsStarted() {
        return unmodifiableList(testsStarted);
    }

    public List<Description> getTestsFinished() {
        return unmodifiableList(testsFinished);
    }

    public List<Failure> getFailures() {
        return unmodifiableList(failures);
    }

    public List<Failure> getAssumptionFailures() {
        return unmodifiableList(assumptionFailures);
    }

    public List<Description> getTestsIgnored() {
        return unmodifiableList(testsIgnored);
    }

    @Override
    public String toString() {
        return testRunsStarted.size() + " test runs started, "
                + testRunsFinished.size() + " test runs finished, "
                + testSuitesStarted.size() + " test suites started, "
                + testSuitesFinished.size() + " test suites finished, "
                + testsStarted.size() + " tests started, "
                + testsFinished.size() + " tests finished, "
                + failures.size() + " failures, " + assumptionFailures.size()
                + " assumption failures, " + testsIgnored.size()
                + " tests ignored";
    }
}
