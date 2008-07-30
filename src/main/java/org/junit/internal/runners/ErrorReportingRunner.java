package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

//TODO: inline the superclass, extend Runner, and then have all ParentRunners validate once again
public class ErrorReportingRunner extends Runner {
	private final List<Throwable> fCauses;

	private final Class<?> fTestClass;

	public ErrorReportingRunner(Class<?> testClass, Throwable cause) {
		fTestClass= testClass;
		fCauses= getCauses(cause);
	}

	@Override
	public Description getDescription() {
		Description description= Description.createSuiteDescription(fTestClass);
		for (Throwable each : fCauses)
			description.addChild(describeCause(each));
		return description;
	}

	@Override
	public void run(RunNotifier notifier) {
		for (Throwable each : fCauses)
			runCause(each, notifier);
	}

	private List<Throwable> getCauses(Throwable cause) {
		if (cause instanceof InvocationTargetException)
			return getCauses(cause.getCause());
		if (cause instanceof InitializationError)
			return ((InitializationError) cause).getCauses();
		return Arrays.asList(cause);
	}

	private Description describeCause(Throwable child) {
		return Description.createTestDescription(fTestClass,
				"initializationError");
	}

	private void runCause(Throwable child, RunNotifier notifier) {
		Description description= describeCause(child);
		notifier.fireTestStarted(description);
		notifier.fireTestFailure(new Failure(description, child));
		notifier.fireTestFinished(description);
	}
}