package org.junit.internal.runners;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;

public class TestMethod {

	private final Method fMethod;
	private TestClass fTestClass;

	public TestMethod(Method method, TestClass testClass) {
		fMethod= method;
		fTestClass= testClass;
	}

	public boolean isIgnored() {
		return fMethod.getAnnotation(Ignore.class) != null;
	}

	public long getTimeout() {
		Test annotation= fMethod.getAnnotation(Test.class);
		long timeout= annotation.timeout();
		return timeout;
	}

	Class<? extends Throwable> getExpectedException() {
		Test annotation= fMethod.getAnnotation(Test.class);
		if (annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

	boolean isUnexpected(Throwable exception) {
		return ! getExpectedException().isAssignableFrom(exception.getClass());
	}

	boolean expectsException() {
		return getExpectedException() != null;
	}

	List<Method> getBefores() {
		return fTestClass.getAnnotatedMethods(Before.class);
	}

	List<Method> getAfters() {
		return fTestClass.getAnnotatedMethods(After.class);
	}

}
