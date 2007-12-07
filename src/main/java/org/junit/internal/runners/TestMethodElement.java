package org.junit.internal.runners;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.internal.runners.model.TestClass;
import org.junit.internal.runners.model.TestElement;
import org.junit.internal.runners.model.FrameworkMethod;

// TODO: (Nov 14, 2007 11:49:52 AM) Does this belong in this package?

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
