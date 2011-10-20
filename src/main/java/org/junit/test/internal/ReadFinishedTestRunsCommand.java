package org.junit.test.internal;

import java.util.List;

import org.junit.runner.Result;
import org.junit.test.EventCollector;

public class ReadFinishedTestRunsCommand implements ReadEventsCommand<Result> {
	public String getName() {
		return "finished test run";
	}

	public List<Result> getEventsFromEventCollector(
			EventCollector eventCollector) {
		return eventCollector.getFinishedTestRuns();
	}
}
