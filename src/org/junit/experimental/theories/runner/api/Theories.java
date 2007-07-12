/**
 * 
 */
package org.junit.experimental.theories.runner.api;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.experimental.theories.methods.api.Theory;
import org.junit.experimental.theories.runner.TheoryMethod;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.TestMethod;

@SuppressWarnings("restriction")
public class Theories extends JUnit4ClassRunner {
	@Override
	protected void validate() throws InitializationError {
	}

	public Theories(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List<Method> getTestMethods() {
		List<Method> testMethods= super.getTestMethods();
		testMethods.addAll(getTestClass().getAnnotatedMethods(Theory.class));
		return testMethods;
	}

	@Override
	protected TestMethod wrapMethod(final Method method) {
		return new TheoryMethod(method, getTestClass());
	}
}