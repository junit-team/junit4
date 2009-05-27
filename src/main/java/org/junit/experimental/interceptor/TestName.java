package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;

public class TestName extends TestWatchman {
	private String fName;

	@Override
	public void starting(FrameworkMethod method) {
		fName = method.getName();
	}
	
	public String getMethodName() {
		return fName;
	}
}
