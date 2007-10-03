/**
 * 
 */
package org.junit.experimental.theories.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.internal.runners.ExplosiveMethod;
import org.junit.internal.runners.Roadie;
import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.JUnit4MethodRunner;

public class TheoryMethodRunner extends JUnit4MethodRunner {
	private List<AssumptionViolatedException> fInvalidParameters= new ArrayList<AssumptionViolatedException>();

	private int successes= 0;

	protected Throwable thrown= null;

	public TheoryMethodRunner(Method method, TestClass testClass) {
		super(method, testClass);
	}

	@Override
	protected void runInsideNotification(Roadie context) {
		runWithExpectedExceptionCheck(context);
	}

	// TODO: (Oct 3, 2007 10:12:34 AM) This is behavior-preserving, but what I want?
	@Override
	public void runInsideExpectedExceptionCheck(Roadie context) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		try {
			runWithAssignment(Assignments.allUnassigned(context, fTestMethod.getMethod()));
		} catch (Throwable e) {
			throw new InvocationTargetException(e);
		}

		if (successes == 0)
			Assert
					.fail("Never found parameters that satisfied method.  Violated assumptions: "
							+ fInvalidParameters);
	}

	protected void runWithAssignment(Assignments parameterAssignment)
			throws Throwable {
		if (!parameterAssignment.isComplete()) {
			runWithIncompleteAssignment(parameterAssignment);
		} else {
			runWithCompleteAssignment(parameterAssignment);
		}
	}

	protected void runWithIncompleteAssignment(Assignments incomplete)
			throws InstantiationException, IllegalAccessException, Throwable {
		for (PotentialAssignment source : incomplete
				.potentialsForNextUnassigned()) {
			runWithAssignment(incomplete.assignNext(source));
		}
	}

	protected void runWithCompleteAssignment(Assignments complete)
			throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, Throwable {
		try {
			final Object[] values= complete.getActualValues(nullsOk());
			final Object freshInstance= complete.getTarget().getClass()
					.getConstructor().newInstance();
			final Roadie thisContext= complete.getContext().withNewInstance(
					freshInstance);
			thisContext.runProtected(this, new Runnable() {
				public void run() {
					try {
						invokeWithActualParameters(freshInstance, values);
					} catch (Throwable e) {
						thrown= e;
					}
				}
			});
			if (thrown != null)
				throw thrown;
		} catch (CouldNotGenerateValueException e) {
		}
	}

	private void invokeWithActualParameters(Object target, Object... params)
			throws Throwable {
		try {
			ExplosiveMethod.from(fTestMethod.getMethod()).invoke(target, params);
			successes++;
		} catch (AssumptionViolatedException e) {
			handleAssumptionViolation(e);
		} catch (Throwable e) {
			reportParameterizedError(e, params);
		}
	}

	protected void handleAssumptionViolation(AssumptionViolatedException e) {
		fInvalidParameters.add(e);
	}

	protected void reportParameterizedError(Throwable e, Object... params)
			throws Throwable {
		if (params.length == 0)
			throw e;
		throw new ParameterizedAssertionError(e, fTestMethod.getMethod().getName(), params);
	}

	private boolean nullsOk() {
		Theory annotation= fTestMethod.getMethod().getAnnotation(Theory.class);
		if (annotation == null)
			return false;
		return annotation.nullsAccepted();
	}
}