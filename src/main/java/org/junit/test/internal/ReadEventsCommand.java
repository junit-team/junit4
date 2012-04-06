package org.junit.test.internal;

import java.util.List;

import org.junit.test.EventCollector;

public interface ReadEventsCommand<T> {
	String getName();

	List<T> getEventsFromEventCollector(EventCollector eventCollector);
}
