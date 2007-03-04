package org.junit.internal.runners;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class ErrorReportingRunner extends Runner {
	private final Description fDescription;

	private final Throwable fCause;

	public ErrorReportingRunner(Description description, Throwable cause) {
		fDescription= description;
		fCause= cause;
	}

	@Override
	public Description getDescription() {
		return fDescription;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.testAborted(fDescription, fCause);
	}
}