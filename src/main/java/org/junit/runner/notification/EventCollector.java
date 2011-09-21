package org.junit.runner.notification;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;

public class EventCollector extends RunListener {
	private final List<Description> testRunsStarted= new ArrayList<Description>();

	private final List<Result> testRunsFinished= new ArrayList<Result>();

	private final List<Description> testsStarted= new ArrayList<Description>();

	private final List<Description> testsFinished= new ArrayList<Description>();

	private final List<Failure> failures= new ArrayList<Failure>();

	private final List<Failure> violatedAssumptions= new ArrayList<Failure>();

	private final List<Description> testsIgnored= new ArrayList<Description>();

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
		violatedAssumptions.add(failure);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		testsIgnored.add(description);
	}
}
