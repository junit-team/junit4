package org.junit.internal.requests;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
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
		List<Throwable> goofs= getCauses(fCause);
		CompositeRunner runner= new CompositeRunner(fClass.getName());
		for (int i= 0; i < goofs.size(); i++) {
			final Description description= Description.createTestDescription(fClass, "initializationError" + i);
			final Throwable throwable= goofs.get(i);
			runner.add(new ErrorReportingRunner(description, throwable));
		}
		return runner;
	}
	
	private List<Throwable> getCauses(Throwable cause) {
		if (cause instanceof InvocationTargetException)
			return getCauses(cause.getCause());
		if (cause instanceof InitializationError)
			return ((InitializationError) cause).getCauses();
		// TODO: untested
		return Arrays.asList(cause);	
	}
}
