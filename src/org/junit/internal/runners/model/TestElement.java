package org.junit.internal.runners.model;

import java.util.List;

import org.junit.Assume.AssumptionViolatedException;

public abstract class TestElement {
	public abstract List<TestMethod> getAfters();

	public abstract List<TestMethod> getBefores();

	public boolean runBefores(Roadie roadie) {
		// TODO: (Oct 5, 2007 11:29:38 AM) just throw the exception!
	
		try {
			List<TestMethod> befores= getBefores();
			for (TestMethod before : befores)
				before.invokeExplosively(roadie.fTarget);
			return true;
		} catch (AssumptionViolatedException e) {
			return false;
		} catch (Throwable e) {
			roadie.addFailure(e);
			return false;
		}
	}

	public void runAfters(Roadie roadie) {
		List<TestMethod> afters= getAfters();
		for (TestMethod after : afters)
			try {
				after.invokeExplosively(roadie.fTarget);
			} catch (Throwable e) {
				roadie.addFailure(e);
			}
	}

	public void runProtected(Roadie roadie, Runnable runnable) {
		try {
			if (runBefores(roadie))
				runnable.run();
		} finally {
			runAfters(roadie);
		}
	}
}
