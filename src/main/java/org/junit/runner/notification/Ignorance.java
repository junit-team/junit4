package org.junit.runner.notification;

import org.junit.runner.Description;

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
