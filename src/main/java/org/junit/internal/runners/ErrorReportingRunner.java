package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class ErrorReportingRunner extends ParentRunner<Throwable> {
	private final Throwable fCause;

	public ErrorReportingRunner(Class<?> type, Throwable cause) {
		super(type);
		fCause= cause;
	}

	@Override
	protected Description describeChild(Throwable child) {
		return Description.createTestDescription(getTestClass().getJavaClass(), "initializationError");
	}
	
	@Override
	protected Statement classBlock(RunNotifier notifier) {
		// no before or after class
		return runChildren(notifier);
	}

	@Override
	protected List<Throwable> getChildren() {
		return getCauses(fCause);
	}

	@Override
	protected void runChild(Throwable child, RunNotifier notifier) {
		Description description= describeChild(child);
		notifier.fireTestStarted(description);
		notifier.fireTestFailure(new Failure(description, child));
		notifier.fireTestFinished(description);
	}
	
	private List<Throwable> getCauses(Throwable cause) {
		if (cause instanceof InvocationTargetException)
			return getCauses(cause.getCause());
		if (cause instanceof InitializationError)
			return ((InitializationError) cause).getCauses();
		return Arrays.asList(cause);	
	}
}