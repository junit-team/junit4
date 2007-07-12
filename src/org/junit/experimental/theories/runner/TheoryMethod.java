/**
 * 
 */
package org.junit.experimental.theories.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.methods.api.Theory;
import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;

public class TheoryMethod extends TestMethod {
	private final Method fMethod;

	private List<AssumptionViolatedException> fInvalidParameters= new ArrayList<AssumptionViolatedException>();

	public TheoryMethod(Method method, TestClass testClass) {
		super(method, testClass);
		fMethod= method;
	}

	@Override
	public void invoke(Object test) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		TheoryContainerReference container= new TheoryContainerReference(
				test);

		ConcreteFunction function= new ConcreteFunction(test, fMethod);

		int runCount= 0;
		try {
			runCount+= container.runWithParameters(this,
					new ArrayList<Object>(), function.signatures());
		} catch (Throwable e) {
			throw new InvocationTargetException(e);
		}
		if (runCount == 0)
			Assert
					.fail("Never found parameters that satisfied method.  Violated assumptions: "
							+ fInvalidParameters);
	}

	public boolean nullsOk() {
		return fMethod.getAnnotation(Theory.class).nullsAccepted();
	}

	public Method getMethod() {
		return fMethod;
	}

	public void addAssumptionFailure(AssumptionViolatedException e) {
		fInvalidParameters.add(e);
	}
}