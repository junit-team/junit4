package org.junit.tests.experimental.theories.extendingwithstubs;


import java.lang.reflect.Method;

import org.junit.experimental.theories.Theories;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestMethod;

public class StubbedTheories extends Theories {
	public StubbedTheories(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected TestMethod wrapMethod(Method method) {
		return new StubbedTheoryMethod(method, getTestClass());
	}
}
