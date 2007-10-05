package org.junit.tests.experimental.theories.extendingwithstubs;


import java.lang.reflect.Method;

import org.junit.experimental.theories.Theories;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4MethodRunner;

public class StubbedTheories extends Theories {
	public StubbedTheories(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected JUnit4MethodRunner wrapMethod(Method method) {
		return new StubbedTheoryMethodRunner(method, getTestClass());
	}
}
