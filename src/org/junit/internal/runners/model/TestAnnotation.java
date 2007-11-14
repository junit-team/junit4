package org.junit.internal.runners.model;

import org.junit.Test;
import org.junit.Test.None;

public class TestAnnotation {
	public Test fAnnotation;

	public TestAnnotation(FrameworkMethod method) {
		fAnnotation= method.getMethod().getAnnotation(Test.class);
	}

	public Class<? extends Throwable> getExpectedException() {
		if (fAnnotation == null || fAnnotation.expected() == None.class)
			return null;
		else
			return fAnnotation.expected();
	}

	public boolean expectsException() {
		return getExpectedException() != null;
	}

	public long getTimeout() {
		if (fAnnotation == null)
			return 0;
		return fAnnotation.timeout();
	}
}