package org.junit.test.internal;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.test.EventCollector;

public class AtLeastOnEvent<T> extends BaseMatcher<EventCollector> {
	private final ReadEventsCommand<T> fReadEventsCommand;
	private final Matcher<T> fMatcher;

	public AtLeastOnEvent(ReadEventsCommand<T> readEventsCommand,
			Matcher<T> matcher) {
		fReadEventsCommand= readEventsCommand;
		fMatcher= matcher;
	}

	public boolean matches(Object item) {
		EventCollector collector= (EventCollector) item;
		return (collector != null) && matchesAtLeastOneEvent(collector);
	}

	private boolean matchesAtLeastOneEvent(EventCollector collector) {
		List<T> events= fReadEventsCommand
				.getEventsFromEventCollector(collector);
		for (T event : events)
			if (fMatcher.matches(event))
				return true;
		return false;
	}

	public void describeTo(Description description) {
		description.appendText(fReadEventsCommand.getName());
		description.appendText(": ");
		description.appendDescriptionOf(fMatcher);
	}
}
