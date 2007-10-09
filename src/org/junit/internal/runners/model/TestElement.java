package org.junit.internal.runners.model;

import java.util.List;

import org.junit.Assume.AssumptionViolatedException;

public abstract class TestElement {
	public abstract List<TestMethod> getAfters();

	public abstract List<TestMethod> getBefores();

	public boolean runBefores(EachTestNotifier roadie, Object target) {
		// TODO: (Oct 5, 2007 11:29:38 AM) just throw the exception!  Should work the same way
	
		try {
			List<TestMethod> befores= getBefores();
			for (TestMethod before : befores)
				before.invokeExplosively(target);
			return true;
		} catch (AssumptionViolatedException e) {
			return false;
		} catch (Throwable e) {
			roadie.addFailure(e);
			return false;
		}
	}

	public void runAfters(EachTestNotifier roadie, Object target) {
		List<TestMethod> afters= getAfters();
		for (TestMethod after : afters)
			try {
				after.invokeExplosively(target);
			} catch (Throwable e) {
				roadie.addFailure(e);
			}
	}

	public void runProtected(EachTestNotifier roadie, Runnable runnable, Object target) {
		try {
			if (runBefores(roadie, target))
				runnable.run();
		} finally {
			runAfters(roadie, target);
		}
	}
}
