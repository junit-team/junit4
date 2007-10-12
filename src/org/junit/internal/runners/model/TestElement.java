package org.junit.internal.runners.model;

import java.util.List;

public abstract class TestElement {
	public abstract List<TestMethod> getAfters();

	public abstract List<TestMethod> getBefores();

	public void runBefores(Object target) throws Throwable {
			List<TestMethod> befores= getBefores();
			for (TestMethod before : befores)
				before.invokeExplosively(target);
	}

	public void runAfters(Object target) throws MultipleFailureException {
		MultipleFailureException errors= new MultipleFailureException();
		
		List<TestMethod> afters= getAfters();
		for (TestMethod after : afters)
			try {
				after.invokeExplosively(target);
			} catch (Throwable e) {
				errors.add(e);
			}
			
		errors.assertEmpty();
	}

	public void runProtected(EachTestNotifier roadie, Runnable runnable, Object target) throws Throwable {
		// TODO: (Oct 12, 2007 10:18:36 AM) DUP with WithBefores and WithAfters
		// TODO: (Oct 12, 2007 10:19:09 AM) Don't need roadie

		
		try {
			runBefores(target);
			runnable.run();
		} finally {
			runAfters(target);
		}
	}
}
