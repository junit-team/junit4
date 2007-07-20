/**
 * 
 */
package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
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

	protected void addFailure(Throwable targetException) {
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

	void runBefores(JavaElement javaElement) throws FailedBefore {
		try {
			try {
				List<Method> befores= javaElement.getBefores();
				for (Method before : befores)
					before.invoke(getTarget());
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		} catch (AssumptionViolatedException e) {
			throw new FailedBefore();
		} catch (Throwable e) {
			addFailure(e);
			throw new FailedBefore();
		}
	}

	void runAfters(JavaElement javaElement) {
		List<Method> afters= javaElement.getAfters();
		for (Method after : afters)
			try {
				after.invoke(getTarget());
			} catch (InvocationTargetException e) {
				addFailure(e.getTargetException());
			} catch (Throwable e) {
				addFailure(e); // Untested, but seems impossible
			}
	}

	public void runProtected(JavaElement javaElement, Runnable runnable) {
		try {
			runBefores(javaElement);
			runnable.run();
		} catch (FailedBefore e) {
		} finally {
			runAfters(javaElement);
		}
	}
}