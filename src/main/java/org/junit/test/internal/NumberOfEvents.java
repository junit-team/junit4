package org.junit.test.internal;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.test.EventCollector;

public class NumberOfEvents<T> extends BaseMatcher<EventCollector> {
	private final ReadEventsCommand<T> fReadEventsCommand;

	private final int fNumberOfEvents;

	public NumberOfEvents(ReadEventsCommand<T> readEventsCommand,
			int numberOfEvents) {
		fReadEventsCommand= readEventsCommand;
		fNumberOfEvents= numberOfEvents;
	}

	public boolean matches(Object item) {
		EventCollector collector= (EventCollector) item;
		return (collector != null) && hasExpectedNumberOfEvents(collector);
	}

	private boolean hasExpectedNumberOfEvents(EventCollector collector) {
		List<T> events= fReadEventsCommand
				.getEventsFromEventCollector(collector);
		return events.size() == fNumberOfEvents;
	}

	public void describeTo(Description description) {
		description.appendText(Integer.toString(fNumberOfEvents));
		description.appendText(" ");
		description.appendText(fReadEventsCommand.getName());
		description.appendText("s");
	}
}
