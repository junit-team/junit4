/**
 * 
 */
package org.junit.experimental.theories;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.experimental.theories.internal.TheoryMethodRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.JUnit4MethodRunner;

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
		// TODO: (Jul 20, 2007 2:02:44 PM) Only get methods once

		List<Method> testMethods= super.getTestMethods();
		testMethods.addAll(getTestClass().getAnnotatedMethods(Theory.class));
		return testMethods;
	}

	@Override
	protected JUnit4MethodRunner wrapMethod(final Method method) {
		return new TheoryMethodRunner(method, getTestClass());
	}
}