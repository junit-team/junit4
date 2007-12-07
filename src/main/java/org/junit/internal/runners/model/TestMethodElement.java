package org.junit.internal.runners.model;

import java.util.List;

import org.junit.After;
import org.junit.Before;

public class TestMethodElement extends TestElement {
	private TestClass fTestClass;

	public TestMethodElement(TestClass testClass) {
		fTestClass = testClass;
	}
	
	@Override
	public List<FrameworkMethod> getBefores() {
		return fTestClass.getAnnotatedMethods(Before.class);
	}

	@Override
	public List<FrameworkMethod> getAfters() {
		return fTestClass.getAnnotatedMethods(After.class);
	}	
}
