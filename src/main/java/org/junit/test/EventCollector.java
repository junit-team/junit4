package org.junit.test;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.test.internal.ReadEventsCommand;
import org.junit.test.internal.ReadFailedAssumptionsCommand;
import org.junit.test.internal.ReadFailuresCommand;
import org.junit.test.internal.ReadFinishedTestRunsCommand;
import org.junit.test.internal.ReadFinishedTestsCommand;
import org.junit.test.internal.ReadIgnoredTestsCommand;
import org.junit.test.internal.ReadStartedTestRunsCommand;
import org.junit.test.internal.ReadStartedTestsCommand;

/**
 * The {@code EventCollector} is a simple {@link RunListener}, that collects all
 * the events of a test run.
 */
public class EventCollector extends RunListener {
	private final List<Description> fTestRunsStarted= new ArrayList<Description>();

	private final List<Result> fTestRunsFinished= new ArrayList<Result>();

	private final List<Description> fTestsStarted= new ArrayList<Description>();

	private final List<Description> fTestsFinished= new ArrayList<Description>();

	private final List<Failure> fFailures= new ArrayList<Failure>();

	private final List<Failure> fFailedAssumptions= new ArrayList<Failure>();

	private final List<Description> fTestsIgnored= new ArrayList<Description>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunStarted(Description description) throws Exception {
		fTestRunsStarted.add(description);
	}

	/**
	 * Returns a list of the {@code Description}s of all started test runs.
	 * 
	 * @return a list of the started test runs.
	 */
	public List<Description> getStartedTestRuns() {
		return new ArrayList<Description>(fTestRunsStarted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunFinished(Result result) throws Exception {
		fTestRunsFinished.add(result);
	}

	/**
	 * Returns a list of the {@code Result}s of all finished test runs.
	 * 
	 * @return a list of the finished test runs.
	 */
	public List<Result> getFinishedTestRuns() {
		return new ArrayList<Result>(fTestRunsFinished);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testStarted(Description description) throws Exception {
		fTestsStarted.add(description);
	}

	/**
	 * Returns a list of the {@code Description}s of all started tests.
	 * 
	 * @return a list of the started tests.
	 */
	public List<Description> getStartedTests() {
		return new ArrayList<Description>(fTestsStarted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testFinished(Description description) throws Exception {
		fTestsFinished.add(description);
	}

	/**
	 * Returns a list of the {@code Description}s of all finished tests.
	 * 
	 * @return a list of the finished tests.
	 */
	public List<Description> getFinishedTests() {
		return new ArrayList<Description>(fTestsFinished);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testFailure(Failure failure) throws Exception {
		fFailures.add(failure);
	}

	/**
	 * Returns a list of all {@code Failure}s.
	 * 
	 * @return a list of the failures.
	 */
	public List<Failure> getFailures() {
		return new ArrayList<Failure>(fFailures);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testAssumptionFailure(Failure failure) {
		fFailedAssumptions.add(failure);
	}

	/**
	 * Returns a list of the {@code Failure}s fo all failed assumptions.
	 * 
	 * @return a list of the failed assumptions.
	 */
	public List<Failure> getFailedAssumptions() {
		return new ArrayList<Failure>(fFailedAssumptions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testIgnored(Description description) throws Exception {
		fTestsIgnored.add(description);
	}

	/**
	 * Returns a list of the {@code Description}s of all ignored tests.
	 * 
	 * @return a list of the ignored tests.
	 */
	public List<Description> getIgnoredTests() {
		return new ArrayList<Description>(fTestsIgnored);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).toString();
	}

	private static class ToStringBuilder {
		@SuppressWarnings("unchecked")
		static final List<ReadEventsCommand<? extends Serializable>> READ_EVENTS_COMMANDS= asList(
				new ReadStartedTestRunsCommand(),
				new ReadFinishedTestRunsCommand(),
				new ReadStartedTestsCommand(), new ReadFinishedTestsCommand(),
				new ReadFailedAssumptionsCommand(), new ReadFailuresCommand(),
				new ReadIgnoredTestsCommand());

		StringBuilder sb= new StringBuilder();

		ToStringBuilder(EventCollector eventCollector) {
			for (ReadEventsCommand<?> command : READ_EVENTS_COMMANDS) {
				addItemsWithHeadline(command, eventCollector);
			}
		}

		void addItemsWithHeadline(ReadEventsCommand<?> command,
				EventCollector eventCollector) {
			List<?> items= command.getEventsFromEventCollector(eventCollector);
			String headline= command.getName() + "s";
			addItemsWithHeadline(items, headline);
		}

		void addItemsWithHeadline(List<?> items, String headline) {
			if (items.isEmpty())
				noItemsWithHeadline(headline);
			else {
				numberOfItemsWithHeadline(items, headline);
				appendItems(items);
			}
		}

		void noItemsWithHeadline(String headline) {
			sb.append("no " + headline + "\n");
		}

		void numberOfItemsWithHeadline(List<?> items, String headline) {
			sb.append(items.size() + " " + headline + "\n");
		}

		void appendItems(List<?> items) {
			for (Object item : items) {
				sb.append(" * ");
				sb.append(item.toString());
				sb.append("\n");
			}
		}

		@Override
		public String toString() {
			return sb.toString();
		}
	}
}
