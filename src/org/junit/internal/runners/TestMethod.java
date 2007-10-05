package org.junit.internal.runners;

import java.lang.reflect.Method;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;

public class TestMethod {
	private final Method fMethod;

	public TestMethod(Method method) {
		fMethod= method;
	}

	public Method getMethod() {
		return fMethod;
	}

	protected Class<? extends Throwable> getExpectedException() {
		Test annotation= getMethod().getAnnotation(Test.class);
		if (annotation == null || annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

	boolean isUnexpected(Throwable exception) {
		return !getExpectedException().isAssignableFrom(exception.getClass());
	}

	boolean expectsException() {
		return getExpectedException() != null;
	}

	public long getTimeout() {
		Test annotation= getMethod().getAnnotation(Test.class);
		if (annotation == null)
			return 0;
		long timeout= annotation.timeout();
		return timeout;
	}

	public boolean isIgnored() {
		return getMethod().getAnnotation(Ignore.class) != null;
	}
}