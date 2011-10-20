package org.junit.test.internal;

import java.util.List;

import org.junit.runner.Description;
import org.junit.test.EventCollector;

public class ReadStartedTestsCommand implements ReadEventsCommand<Description> {
	public String getName() {
		return "started test";
	}

	public List<Description> getEventsFromEventCollector(
			EventCollector eventCollector) {
		return eventCollector.getStartedTests();
	}
}
