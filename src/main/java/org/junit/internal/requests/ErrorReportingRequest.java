package org.junit.internal.requests;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class ErrorReportingRequest extends Request {

	private final Class<?> fClass;
	private final Throwable fCause;

	public ErrorReportingRequest(Class<?> klass, Throwable cause) {
		fClass= klass;
		fCause= cause;
	}

	@Override
	public Runner getRunner() {
		// TODO: (Dec 10, 2007 9:41:13 PM) Should this class exist?

		return new ErrorReportingRunner(fClass, fCause);
	}
}
