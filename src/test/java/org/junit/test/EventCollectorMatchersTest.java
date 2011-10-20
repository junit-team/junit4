package org.junit.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.runner.Description.createSuiteDescription;
import static org.junit.test.EventCollectorMatchers.failedAssumption;
import static org.junit.test.EventCollectorMatchers.failure;
import static org.junit.test.EventCollectorMatchers.finishedTest;
import static org.junit.test.EventCollectorMatchers.finishedTestRun;
import static org.junit.test.EventCollectorMatchers.ignoredTest;
import static org.junit.test.EventCollectorMatchers.numberOfFailedAssumptions;
import static org.junit.test.EventCollectorMatchers.numberOfFailures;
import static org.junit.test.EventCollectorMatchers.numberOfFinishedTestRuns;
import static org.junit.test.EventCollectorMatchers.numberOfFinishedTests;
import static org.junit.test.EventCollectorMatchers.numberOfIgnoredTests;
import static org.junit.test.EventCollectorMatchers.numberOfStartedTestRuns;
import static org.junit.test.EventCollectorMatchers.numberOfStartedTests;
import static org.junit.test.EventCollectorMatchers.startedTest;
import static org.junit.test.EventCollectorMatchers.startedTestRun;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class EventCollectorMatchersTest {
	private static final Description ARBITRARY_DESCRIPTION= createSuiteDescription("arbtirary name");

	private static final Result ARBITRARY_RESULT= new Result();

	private static final AssertionError ARBITRARY_EXCEPTION= new AssertionError();

	private static final Failure ARBITRARY_FAILURE= new Failure(
			ARBITRARY_DESCRIPTION, ARBITRARY_EXCEPTION);

	private final EventCollector collector= new EventCollector();

	@Test
	public void matchFailedAssumption() throws Exception {
		collector.testAssumptionFailure(ARBITRARY_FAILURE);
		assertThat(collector, failedAssumption(equalTo(ARBITRARY_FAILURE)));
	}

	@Test
	public void matchOneFailedAssumption() throws Exception {
		collector.testAssumptionFailure(ARBITRARY_FAILURE);
		assertThat(collector, numberOfFailedAssumptions(1));
	}

	@Test
	public void matchFailure() throws Exception {
		collector.testFailure(ARBITRARY_FAILURE);
		assertThat(collector, failure(equalTo(ARBITRARY_FAILURE)));
	}

	@Test
	public void matchOneFailure() throws Exception {
		collector.testFailure(ARBITRARY_FAILURE);
		assertThat(collector, numberOfFailures(1));
	}

	@Test
	public void matchFinishedTest() throws Exception {
		collector.testFinished(ARBITRARY_DESCRIPTION);
		assertThat(collector, finishedTest(equalTo(ARBITRARY_DESCRIPTION)));
	}

	@Test
	public void matchOneFinishedTest() throws Exception {
		collector.testFinished(ARBITRARY_DESCRIPTION);
		assertThat(collector, numberOfFinishedTests(1));
	}

	@Test
	public void matchFinishedTestRun() throws Exception {
		collector.testRunFinished(ARBITRARY_RESULT);
		assertThat(collector, finishedTestRun(equalTo(ARBITRARY_RESULT)));
	}

	@Test
	public void matchOneFinishedTestRun() throws Exception {
		collector.testRunFinished(ARBITRARY_RESULT);
		assertThat(collector, numberOfFinishedTestRuns(1));
	}

	@Test
	public void matchIgnoredTest() throws Exception {
		collector.testIgnored(ARBITRARY_DESCRIPTION);
		assertThat(collector, ignoredTest(equalTo(ARBITRARY_DESCRIPTION)));
	}

	@Test
	public void matchOneIgnoredTest() throws Exception {
		collector.testIgnored(ARBITRARY_DESCRIPTION);
		assertThat(collector, numberOfIgnoredTests(1));
	}

	@Test
	public void matchStartedTest() throws Exception {
		collector.testStarted(ARBITRARY_DESCRIPTION);
		assertThat(collector, startedTest(equalTo(ARBITRARY_DESCRIPTION)));
	}

	@Test
	public void matchOneStartedTest() throws Exception {
		collector.testStarted(ARBITRARY_DESCRIPTION);
		assertThat(collector, numberOfStartedTests(1));
	}

	@Test
	public void matchStartedTestRun() throws Exception {
		collector.testRunStarted(ARBITRARY_DESCRIPTION);
		assertThat(collector, startedTestRun(equalTo(ARBITRARY_DESCRIPTION)));
	}

	@Test
	public void matchOneStartedTestRun() throws Exception {
		collector.testRunStarted(ARBITRARY_DESCRIPTION);
		assertThat(collector, numberOfStartedTestRuns(1));
	}
}
