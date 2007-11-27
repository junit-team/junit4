/**
 * 
 */
package org.junit.internal.runners.model;


import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Ignore;
import org.junit.Assume.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class EachTestNotifier {
	private RunNotifier fNotifier;

	private Description fDescription;

	public EachTestNotifier(RunNotifier notifier, Description description) {
		fNotifier= notifier;
		fDescription= description;
	}
	
	// TODO: (Nov 26, 2007 8:51:17 PM) IgnoreClass should include reason


	public void addFailure(Throwable targetException) {
		if (targetException instanceof MultipleFailureException) {
			MultipleFailureException mfe= (MultipleFailureException) targetException;
			for (Throwable each : mfe.getFailures()) {
				addFailure(each);
			}
			return;
		}
		fNotifier.fireTestFailure(new Failure(fDescription, targetException));
	}

	public void fireTestFinished() {
		fNotifier.fireTestFinished(fDescription);
	}

	public void fireTestStarted() {
		fNotifier.fireTestStarted(fDescription);
	}

	public void fireTestIgnored() {
		fNotifier.fireTestIgnored(fDescription);
		fNotifier.fireTestIgnoredReason(fDescription, makeIgnoredException(fDescription));
	}

	private AssumptionViolatedException makeIgnoredException(
			Description description) {
		String reason= description.getAnnotation(Ignore.class).value();
		return new AssumptionViolatedException(reason, nullValue());
	}

	public void addIgnorance(AssumptionViolatedException e) {
		fNotifier.fireTestIgnored(fDescription);
		fNotifier.fireTestIgnoredReason(fDescription, e);
	}
}