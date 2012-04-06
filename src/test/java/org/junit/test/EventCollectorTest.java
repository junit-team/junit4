package org.junit.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runner.Description.createSuiteDescription;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class EventCollectorTest {
	private static final Description DESCRIPTION= createSuiteDescription("any description");
	private static final Failure FAILURE= new Failure(null, null);

	private static final Result RESULT= new Result();
	private final EventCollector collector= new EventCollector();

	@Test
	public void collectTestRunsStarted() throws Exception {
		collector.testRunStarted(DESCRIPTION);
		assertThat(collector.getStartedTestRuns().get(0), is(DESCRIPTION));
	}

	@Test
	public void cannotModifyTestRunsStartedList() throws Exception {
		collector.testRunStarted(DESCRIPTION);
		collector.getStartedTestRuns().clear();
		assertThat(collector.getStartedTestRuns().size(), is(1));
	}

	@Test
	public void collectTestRunsFinished() throws Exception {
		collector.testRunFinished(RESULT);
		assertThat(collector.getFinishedTestRuns().get(0), is(RESULT));
	}

	@Test
	public void cannotModifyTestRunsFinishedList() throws Exception {
		collector.testRunFinished(RESULT);
		collector.getFinishedTestRuns().clear();
		assertThat(collector.getFinishedTestRuns().size(), is(1));
	}

	@Test
	public void collectTestsStarted() throws Exception {
		collector.testStarted(DESCRIPTION);
		assertThat(collector.getStartedTests().get(0), is(DESCRIPTION));
	}

	@Test
	public void cannotModifyTestsStartedList() throws Exception {
		collector.testStarted(DESCRIPTION);
		collector.getStartedTests().clear();
		assertThat(collector.getStartedTests().size(), is(1));
	}

	@Test
	public void collectTestsFinished() throws Exception {
		collector.testFinished(DESCRIPTION);
		assertThat(collector.getFinishedTests().get(0), is(DESCRIPTION));
	}

	@Test
	public void cannotModifyTestsFinishedList() throws Exception {
		collector.testFinished(DESCRIPTION);
		collector.getFinishedTests().clear();
		assertThat(collector.getFinishedTests().size(), is(1));
	}

	@Test
	public void collectFailure() throws Exception {
		collector.testFailure(FAILURE);
		assertThat(collector.getFailures().get(0), is(FAILURE));
	}

	@Test
	public void cannotModifyFailureList() throws Exception {
		collector.testFailure(FAILURE);
		collector.getFailures().clear();
		assertThat(collector.getFailures().size(), is(1));
	}

	@Test
	public void collectAssumptionFailure() throws Exception {
		collector.testAssumptionFailure(FAILURE);
		assertThat(collector.getFailedAssumptions().get(0), is(FAILURE));
	}

	@Test
	public void cannotModifyAssumptionFailureList() throws Exception {
		collector.testAssumptionFailure(FAILURE);
		collector.getFailedAssumptions().clear();
		assertThat(collector.getFailedAssumptions().size(), is(1));
	}

	@Test
	public void collectTestsIgnored() throws Exception {
		collector.testIgnored(DESCRIPTION);
		assertThat(collector.getIgnoredTests().get(0), is(DESCRIPTION));
	}

	@Test
	public void cannotModifyTestsIgnoredList() throws Exception {
		collector.testIgnored(DESCRIPTION);
		collector.getIgnoredTests().clear();
		assertThat(collector.getIgnoredTests().size(), is(1));
	}
}
