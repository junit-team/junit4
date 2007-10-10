package org.junit.internal.runners.model;

import java.util.List;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.FailureListener;

public abstract class TestElement {
	public abstract List<TestMethod> getAfters();

	public abstract List<TestMethod> getBefores();

	public boolean runBefores(FailureListener listener, Object target) {
		try {
			List<TestMethod> befores= getBefores();
			for (TestMethod before : befores)
				before.invokeExplosively(target);
			return true;
		} catch (AssumptionViolatedException e) {
			return false;
		} catch (Throwable e) {
			listener.addFailure(e);
			return false;
		}
	}

	public void runAfters(FailureListener roadie, Object target) {
		List<TestMethod> afters= getAfters();
		for (TestMethod after : afters)
			try {
				after.invokeExplosively(target);
			} catch (Throwable e) {
				roadie.addFailure(e);
			}
	}

	public void runProtected(FailureListener roadie, Runnable runnable, Object target) {
		try {
			if (runBefores(roadie, target))
				runnable.run();
		} finally {
			runAfters(roadie, target);
		}
	}
}
