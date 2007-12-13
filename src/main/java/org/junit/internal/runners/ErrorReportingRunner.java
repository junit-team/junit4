package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class ErrorReportingRunner extends ParentRunner<Throwable> {
	private final Throwable fCause;

	private final Class<?> fClass;

	public ErrorReportingRunner(Class<?> type, Throwable cause) {
		super(null);
		// TODO: (Dec 10, 2007 9:41:50 PM) remove fClass

		fClass= type;
		fCause= cause;
	}

	@Override
	protected Description describeChild(Throwable child) {
		return Description.createTestDescription(fClass, "initializationError");
	}
	
	@Override
	protected String getName() {
		// TODO: (Dec 10, 2007 9:53:31 PM) DUP with superclass?

		return fClass.getName();
	}
	
	@Override
	protected Annotation[] classAnnotations() {
		// TODO: (Dec 10, 2007 9:54:09 PM) DUP with other ParentRunner subclass?

		return new Annotation[0];
	}

	@Override
	protected List<Throwable> getChildren() {
		return getCauses(fCause);
	}

	@Override
	protected void runChild(Throwable child, RunNotifier notifier) {
		notifier.testAborted(describeChild(child), child);
	}
	
	private List<Throwable> getCauses(Throwable cause) {
		if (cause instanceof InvocationTargetException)
			return getCauses(cause.getCause());
		if (cause instanceof InitializationError)
			return ((InitializationError) cause).getCauses();
		return Arrays.asList(cause);	
	}
}