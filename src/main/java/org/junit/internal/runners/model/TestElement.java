package org.junit.internal.runners.model;

import java.util.List;

public abstract class TestElement {
	public abstract List<FrameworkMethod> getAfters();

	public abstract List<FrameworkMethod> getBefores();
	
	//TODO I inlined runBefores() for symmetry with runAfters. Either this or both methods should be here.
}
