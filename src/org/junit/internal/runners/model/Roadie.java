/**
 * 
 */
package org.junit.internal.runners.model;


import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class Roadie {
	Object fTarget;

	private RunNotifier fNotifier;

	private Description fDescription;

	public Roadie(RunNotifier notifier, Description description,
			Object target) {
		fTarget= target;
		fNotifier= notifier;
		fDescription= description;
	}

	public void addFailure(Throwable targetException) {
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
	}

	public Object getTarget() {
		return fTarget;
	}

	public Roadie withNewInstance(Object freshInstance) {
		return new Roadie(fNotifier, fDescription, freshInstance);
	}
}