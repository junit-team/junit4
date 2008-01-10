package org.junit.runner;

import org.junit.runner.notification.TestRunEvent;

// TODO: (Dec 12, 2007 2:39:57 PM) does this belong here?

public class Ignorance extends TestRunEvent {

	private final String fReason;
	private final Description fDescription;

	public Ignorance(Description description, String reason) {
		fDescription= description;
		fReason= reason;
	}

	@Override
	public String getTestHeader() {
		return fDescription + ": " + fReason;
	}

	@Override
	public String getTrace() {
		return "";
	}
}
