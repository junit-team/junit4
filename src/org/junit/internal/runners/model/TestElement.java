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

	public void runAfters(Object target) throws Throwable {
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
}
