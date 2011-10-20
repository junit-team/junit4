package org.junit.test.internal;

import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.test.EventCollector;

public class ReadFailedAssumptionsCommand implements ReadEventsCommand<Failure> {
	public String getName() {
		return "failed assumption";
	}

	public List<Failure> getEventsFromEventCollector(
			EventCollector eventCollector) {
		return eventCollector.getFailedAssumptions();
	}
}
