package org.junit.testsupport;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.ExpectedException.none;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class EventCollectorTest {
    private static final Description DUMMY_DESCRIPTION = Description.EMPTY;
    private static final Failure DUMMY_FAILURE = new Failure(null, null);
    private static final Result DUMMY_RESULT = new Result();

    @Rule
    public final ExpectedException thrown = none();

    private final EventCollector collector = new EventCollector();

    @Test
    public void collectsTestRunsStarted() {
        collector.testRunStarted(DUMMY_DESCRIPTION);
        assertEquals(singletonList(DUMMY_DESCRIPTION), collector.getTestRunsStarted());
    }

    @Test
    public void returnsUnmodifiableListOfTestRunsStarted() {
        assertNoDescriptionCanBeAddedToList(collector.getTestRunsStarted());
    }

    @Test
    public void collectsTestRunsFinished() {
        collector.testRunFinished(DUMMY_RESULT);
        assertEquals(singletonList(DUMMY_RESULT), collector.getTestRunsFinished());
    }

    @Test
    public void returnsUnmodifiableListOfTestRunsFinished() {
        assertNoResultCanBeAddedToList(collector.getTestRunsFinished());
    }

    @Test
    public void collectsTestSuitesStarted() {
        collector.testSuiteStarted(DUMMY_DESCRIPTION);
        assertEquals(singletonList(DUMMY_DESCRIPTION), collector.getTestSuitesStarted());
    }

    @Test
    public void returnsUnmodifiableListOfTestSuitesStarted() {
        assertNoDescriptionCanBeAddedToList(collector.getTestSuitesStarted());
    }

    @Test
    public void collectsTestSuitesFinished() {
        collector.testSuiteFinished(DUMMY_DESCRIPTION);
        assertEquals(singletonList(DUMMY_DESCRIPTION), collector.getTestSuitesFinished());
    }

    @Test
    public void returnsUnmodifiableListOfTestSuitesFinished() {
        assertNoDescriptionCanBeAddedToList(collector.getTestSuitesFinished());
    }

    @Test
    public void collectsTestsStarted() {
        collector.testStarted(DUMMY_DESCRIPTION);
        assertEquals(singletonList(DUMMY_DESCRIPTION), collector.getTestsStarted());
    }

    @Test
    public void returnsUnmodifiableListOfTestsStarted() {
        assertNoDescriptionCanBeAddedToList(collector.getTestsStarted());
    }

    @Test
    public void collectsTestsFinished() {
        collector.testFinished(DUMMY_DESCRIPTION);
        assertEquals(singletonList(DUMMY_DESCRIPTION), collector.getTestsFinished());
    }

    @Test
    public void returnsUnmodifiableListOfTestsFinished() {
        assertNoDescriptionCanBeAddedToList(collector.getTestsFinished());
    }

    @Test
    public void collectsFailures() {
        collector.testFailure(DUMMY_FAILURE);
        assertEquals(singletonList(DUMMY_FAILURE), collector.getFailures());
    }

    @Test
    public void returnsUnmodifiableListOfFailures() {
        assertNoFailureCanBeAddedToList(collector.getFailures());
    }

    @Test
    public void collectsAssumptionFailures() {
        collector.testAssumptionFailure(DUMMY_FAILURE);
        assertEquals(singletonList(DUMMY_FAILURE), collector.getAssumptionFailures());
    }

    @Test
    public void returnsUnmodifiableListOfAssumptionFailures() {
        assertNoFailureCanBeAddedToList(collector.getAssumptionFailures());
    }

    @Test
    public void collectsTestsIgnored() {
        collector.testIgnored(DUMMY_DESCRIPTION);
        assertEquals(singletonList(DUMMY_DESCRIPTION), collector.getTestsIgnored());
    }

    @Test
    public void returnsUnmodifiableListOfTestsIgnored() {
        assertNoDescriptionCanBeAddedToList(collector.getTestsIgnored());
    }

    private void assertNoDescriptionCanBeAddedToList(List<Description> list) {
        thrown.expect(Exception.class);
        list.add(DUMMY_DESCRIPTION);
    }

    private void assertNoFailureCanBeAddedToList(List<Failure> list) {
        thrown.expect(Exception.class);
        list.add(DUMMY_FAILURE);
    }

    private void assertNoResultCanBeAddedToList(List<Result> list) {
        thrown.expect(Exception.class);
        list.add(DUMMY_RESULT);
    }
}
