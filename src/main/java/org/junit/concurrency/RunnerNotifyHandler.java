package org.junit.concurrency;

import java.util.concurrent.ExecutionException;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * Notifier for the junit test framework.
 * 
 * @author Christoph Jerolimov
 */
public class RunnerNotifyHandler {
	private final RunNotifier notifier;
	private final Description description;
	
	public RunnerNotifyHandler(RunNotifier notifier, Description description) {
		this.notifier = notifier;
		this.description = description;
	}
	
	public void fireTestIgnored() {
		notifier.fireTestIgnored(description);
	}
	
	public void fireTestStarted() {
		notifier.fireTestStarted(description);
	}
	
	public void fireTestFinished() {
		notifier.fireTestFinished(description);
	}
	
	public void handleException(Throwable throwable) {
		Throwable cause = throwable;
		while (cause instanceof ExecutionException) {
			cause = cause.getCause();
		}
		if (cause instanceof AssumptionViolatedException) {
			// System.out.println("assumption violation: " + cause);
			notifier.fireTestAssumptionFailed(new Failure(description, cause));
		} else {
			// System.out.println("another failure: " + cause);
			notifier.fireTestFailure(new Failure(description, cause));
		}
	}
}
