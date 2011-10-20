package org.junit.test;

import static org.hamcrest.CoreMatchers.allOf;
import org.hamcrest.Matcher;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.test.internal.AtLeastOnEvent;
import org.junit.test.internal.NumberOfEvents;
import org.junit.test.internal.ReadFailedAssumptionsCommand;
import org.junit.test.internal.ReadFailuresCommand;
import org.junit.test.internal.ReadFinishedTestRunsCommand;
import org.junit.test.internal.ReadFinishedTestsCommand;
import org.junit.test.internal.ReadIgnoredTestsCommand;
import org.junit.test.internal.ReadStartedTestRunsCommand;
import org.junit.test.internal.ReadStartedTestsCommand;

/**
 * A collection of hamcrest matchers for the {@link EventCollector}.
 */
public class EventCollectorMatchers {
	private static final ReadFailedAssumptionsCommand READ_FAILED_ASSUMPTIONS= new ReadFailedAssumptionsCommand();

	private static final ReadFailuresCommand READ_FAILURES= new ReadFailuresCommand();

	private static final ReadFinishedTestRunsCommand READ_FINISHED_TEST_RUNS= new ReadFinishedTestRunsCommand();

	private static final ReadFinishedTestsCommand READ_FINISHED_TESTS= new ReadFinishedTestsCommand();

	private static final ReadIgnoredTestsCommand READ_IGNORED_TESTS= new ReadIgnoredTestsCommand();

	private static final ReadStartedTestsCommand READ_STARTED_TESTS= new ReadStartedTestsCommand();

	private static final ReadStartedTestRunsCommand READ_STARTED_TEST_RUNS= new ReadStartedTestRunsCommand();

	/**
	 * This is a shortcut for the frequently used Matcher combination
	 * {@code allOf(noFailures(), noFailedAssumptions(), noIgnoredTests())}
	 */
	@SuppressWarnings("unchecked")
	public static Matcher<EventCollector> onlySuccessfulTests() {
		return allOf(noFailures(), noFailedAssumptions(), noIgnoredTests());
	}

	/**
	 * Evaluates to true if the specified number of failed assumptions occurred.
	 * 
	 * @param numberOfFailedAssumptions
	 *            the expected number of failed assumptions.
	 */
	public static Matcher<EventCollector> numberOfFailedAssumptions(
			int numberOfFailedAssumptions) {
		return new NumberOfEvents<Failure>(READ_FAILED_ASSUMPTIONS,
				numberOfFailedAssumptions);
	}

	/**
	 * Evaluates to true if no assumption failed.
	 */
	public static Matcher<EventCollector> noFailedAssumptions() {
		return numberOfFailedAssumptions(0);
	}

	/**
	 * Evaluates to true if the specified matcher evaluates to true for at least
	 * one of the failed assumptions.
	 */
	public static Matcher<EventCollector> failedAssumption(
			Matcher<Failure> matcher) {
		return new AtLeastOnEvent<Failure>(READ_FAILED_ASSUMPTIONS, matcher);
	}

	/**
	 * Evaluates to true if the specified number of failures occurred.
	 * 
	 * @param numberOfFailures
	 *            the expected number of failures.
	 */
	public static Matcher<EventCollector> numberOfFailures(int numberOfFailures) {
		return new NumberOfEvents<Failure>(READ_FAILURES, numberOfFailures);
	}

	/**
	 * Evaluates to true if no failures occurred.
	 */
	public static Matcher<EventCollector> noFailures() {
		return numberOfFailures(0);
	}

	/**
	 * Evaluates to true if the specified matcher evaluates to true for at least
	 * one of the failures.
	 */
	public static Matcher<EventCollector> failure(Matcher<Failure> matcher) {
		return new AtLeastOnEvent<Failure>(READ_FAILURES, matcher);
	}

	/**
	 * Evaluates to true if the specified number of tests have been finished.
	 * 
	 * @param numberOfFinishedTests
	 *            the expected number of finished tests.
	 */
	public static Matcher<EventCollector> numberOfFinishedTests(
			int numberOfFinishedTests) {
		return new NumberOfEvents<Description>(READ_FINISHED_TESTS,
				numberOfFinishedTests);
	}

	/**
	 * Evaluates to true if no test finished.
	 */
	public static Matcher<EventCollector> noFinishedTests() {
		return numberOfFinishedTests(0);
	}

	/**
	 * Evaluates to true if the specified matcher evaluates to true for at least
	 * one of the finished tests.
	 */
	public static Matcher<EventCollector> finishedTest(
			Matcher<Description> matcher) {
		return new AtLeastOnEvent<Description>(READ_FINISHED_TESTS, matcher);
	}

	/**
	 * Evaluates to true if the specified number of test runs have been
	 * finished.
	 * 
	 * @param numberOfFinishedTestRuns
	 *            the expected number of finished test runs.
	 */
	public static Matcher<EventCollector> numberOfFinishedTestRuns(
			int numberOfFinishedTestRuns) {
		return new NumberOfEvents<Result>(READ_FINISHED_TEST_RUNS,
				numberOfFinishedTestRuns);
	}

	/**
	 * Evaluates to true if no test run finished.
	 */
	public static Matcher<EventCollector> noFinishedTestRuns() {
		return numberOfFinishedTestRuns(0);
	}

	/**
	 * Evaluates to true if the specified matcher evaluates to true for at least
	 * one of the finished test runs.
	 */
	public static Matcher<EventCollector> finishedTestRun(
			Matcher<Result> matcher) {
		return new AtLeastOnEvent<Result>(READ_FINISHED_TEST_RUNS, matcher);
	}

	/**
	 * Evaluates to true if the specified number of tests have been ignored.
	 * 
	 * @param numberOfIgnoredTests
	 *            the expected number of ignored tests.
	 */
	public static Matcher<EventCollector> numberOfIgnoredTests(
			int numberOfIgnoredTests) {
		return new NumberOfEvents<Description>(READ_IGNORED_TESTS,
				numberOfIgnoredTests);
	}

	/**
	 * Evaluates to true if no test has been ignored.
	 */
	public static Matcher<EventCollector> noIgnoredTests() {
		return numberOfIgnoredTests(0);
	}

	/**
	 * Evaluates to true if the specified matcher evaluates to true for at least
	 * one of the ignored tests.
	 */
	public static Matcher<EventCollector> ignoredTest(
			Matcher<Description> matcher) {
		return new AtLeastOnEvent<Description>(READ_IGNORED_TESTS, matcher);
	}

	/**
	 * Evaluates to true if the specified number of tests have been started.
	 * 
	 * @param numberOfStartedTests
	 *            the expected number of started tests.
	 */
	public static Matcher<EventCollector> numberOfStartedTests(
			int numberOfStartedTests) {
		return new NumberOfEvents<Description>(READ_STARTED_TESTS,
				numberOfStartedTests);
	}

	/**
	 * Evaluates to true if no test has been started.
	 */
	public static Matcher<EventCollector> noStartedTests() {
		return numberOfStartedTests(0);
	}

	/**
	 * Evaluates to true if the specified matcher evaluates to true for at least
	 * one of the started tests.
	 */
	public static Matcher<EventCollector> startedTest(
			Matcher<Description> matcher) {
		return new AtLeastOnEvent<Description>(READ_STARTED_TESTS, matcher);
	}

	/**
	 * Evaluates to true if the specified number of test runs have been started.
	 * 
	 * @param numberOfStartedTestRuns
	 *            the expected number of started test runs.
	 */
	public static Matcher<EventCollector> numberOfStartedTestRuns(
			int numberOfStartedTestRuns) {
		return new NumberOfEvents<Description>(READ_STARTED_TEST_RUNS,
				numberOfStartedTestRuns);
	}

	/**
	 * Evaluates to true if no test run has been started.
	 */
	public static Matcher<EventCollector> noStartedTestRuns() {
		return numberOfStartedTestRuns(0);
	}

	/**
	 * Evaluates to true if the specified matcher evaluates to true for at least
	 * one of the started test runs.
	 */
	public static Matcher<EventCollector> startedTestRun(
			Matcher<Description> matcher) {
		return new AtLeastOnEvent<Description>(READ_STARTED_TEST_RUNS, matcher);
	}
}
