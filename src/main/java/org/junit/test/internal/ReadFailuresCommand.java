package org.junit.test.internal;

import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.test.EventCollector;

public class ReadFailuresCommand implements ReadEventsCommand<Failure> {
	public String getName() {
		return "failure";
	}

	public List<Failure> getEventsFromEventCollector(
			EventCollector eventCollector) {
		return eventCollector.getFailures();
	}
}
