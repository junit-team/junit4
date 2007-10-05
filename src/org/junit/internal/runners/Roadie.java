/**
 * 
 */
package org.junit.internal.runners;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class Roadie {
	private Object fTarget;

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

	protected void fireTestFinished() {
		fNotifier.fireTestFinished(fDescription);
	}

	protected void fireTestStarted() {
		fNotifier.fireTestStarted(fDescription);
	}

	protected void fireTestIgnored() {
		fNotifier.fireTestIgnored(fDescription);
	}

	public Object getTarget() {
		return fTarget;
	}

	boolean runBefores(JavaElement javaElement) {
		// TODO: (Oct 5, 2007 11:29:38 AM) just throw the exception!

		try {
			List<Method> befores= javaElement.getBefores();
			for (Method before : befores)
				ExplosiveMethod.from(before).invoke(fTarget);
			return true;
		} catch (AssumptionViolatedException e) {
			return false;
		} catch (Throwable e) {
			addFailure(e);
			return false;
		}
	}

	void runAfters(JavaElement javaElement) {
		List<Method> afters= javaElement.getAfters();
		for (Method after : afters)
			try {
				ExplosiveMethod.from(after).invoke(fTarget);
			} catch (Throwable e) {
				addFailure(e);
			}
	}

	public void runProtected(JavaElement javaElement, Runnable runnable) {
		try {
			if (runBefores(javaElement))
				runnable.run();
		} finally {
			runAfters(javaElement);
		}
	}
	
	public Roadie withNewInstance(Object freshInstance) {
		return new Roadie(fNotifier, fDescription, freshInstance);
	}
}