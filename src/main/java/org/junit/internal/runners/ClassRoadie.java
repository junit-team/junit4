package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * @deprecated Included for backwards compatibility with JUnit 4.4. Will be
 *             removed in the next release. Please use
 *             {@link BlockJUnit4ClassRunner} in place of {@link JUnit4ClassRunner}.
 */
@Deprecated
public
class ClassRoadie {
	private RunNotifier fNotifier;
	private TestClass fTestClass;
	private Description fDescription;
	private final Runnable fRunnable;
	
	public ClassRoadie(RunNotifier notifier, TestClass testClass,
			Description description, Runnable runnable) {
		fNotifier= notifier;
		fTestClass= testClass;
		fDescription= description;
		fRunnable= runnable;
	}

	protected void runUnprotected() {
		fRunnable.run();
	};

	protected void addFailure(Throwable targetException) {
		fNotifier.fireTestFailure(new Failure(fDescription, targetException));
	}

	public void runProtected() {
		try {
			runBefores();
			runUnprotected();
		} catch (FailedBefore e) {
		} finally {
			runAfters();
		}
	}

	private void runBefores() throws FailedBefore {
		try {
			try {
				List<Method> befores= fTestClass.getBefores();
				for (Method before : befores)
					before.invoke(null);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		} catch (org.junit.internal.AssumptionViolatedException e) {
			throw new FailedBefore();
		} catch (Throwable e) {
			addFailure(e);
			throw new FailedBefore();
		}
	}

	private void runAfters() {
		List<Method> afters= fTestClass.getAfters();
		for (Method after : afters)
			try {
				after.invoke(null);
			} catch (InvocationTargetException e) {
				addFailure(e.getTargetException());
			} catch (Throwable e) {
				addFailure(e); // Untested, but seems impossible
			}
	}
}