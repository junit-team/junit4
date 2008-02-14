package org.junit.internal.runners.model;

import java.util.List;

public abstract class TestElement {
	public abstract List<FrameworkMethod> getBefores();

	public abstract List<FrameworkMethod> getAfters();
}
