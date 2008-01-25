package org.junit.internal.runners.model;

import java.util.List;

public abstract class TestElement {
	public abstract List<FrameworkMethod> getAfters();

	public abstract List<FrameworkMethod> getBefores();

	public void runBefores(Object target) throws Throwable {
			List<FrameworkMethod> befores= getBefores();
			for (FrameworkMethod before : befores)
				before.invokeExplosively(target);
	}
}
