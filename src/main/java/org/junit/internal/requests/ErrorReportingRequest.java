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
//		List<Throwable> goofs= getCauses(fCause);
//		CompositeRunner runner= new CompositeRunner(fClass.getName());
//		for (int i= 0; i < goofs.size(); i++) {
//			final Description description= Description.createTestDescription(fClass, "initializationError" + i);
//			final Throwable throwable= goofs.get(i);
//			runner.add(new ErrorReportingRunner(fClass, throwable));
//		}
//		return runner;
	}
	
//	private List<Throwable> getCauses(Throwable cause) {
//		if (cause instanceof InvocationTargetException)
//			return getCauses(cause.getCause());
//		if (cause instanceof InitializationError)
//			return ((InitializationError) cause).getCauses();
//		return Arrays.asList(cause);	
//	}
}
