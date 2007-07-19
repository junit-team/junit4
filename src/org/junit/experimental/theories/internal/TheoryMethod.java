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
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.PotentialParameterValue;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.PotentialParameterValue.CouldNotGenerateValueException;
import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;

public class TheoryMethod extends TestMethod {
	public static class PotentialMethodValues {
		public final List<PotentialParameterValue> fSources;

		public PotentialMethodValues() {
			this(new ArrayList<PotentialParameterValue>());
		}

		public PotentialMethodValues(List<PotentialParameterValue> concat) {
			fSources= concat;
		}

		Object[] getValues(boolean nullsOk) throws CouldNotGenerateValueException {
			Object[] values= new Object[fSources.size()];
			for (int i= 0; i < values.length; i++) {
				values[i]= fSources.get(i).getValue();
				if (values[i] == null && !nullsOk)
					throw new CouldNotGenerateValueException();
			}
			return values;
		}

		PotentialMethodValues concat(PotentialParameterValue source) {
			List<PotentialParameterValue> list= new ArrayList<PotentialParameterValue>();
			list.addAll(fSources);
			list.add(source);
			return new PotentialMethodValues(list);
		}
	}

	private final Method fMethod;

	private List<AssumptionViolatedException> fInvalidParameters= new ArrayList<AssumptionViolatedException>();

	public TheoryMethod(Method method, TestClass testClass) {
		super(method, testClass);
		fMethod= method;
	}

	@Override
	public void invoke(Object test) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		int runCount= 0;
		try {
			runCount+= runWithDiscoveredParameterValues(test,
					new PotentialMethodValues(), ParameterSignature
							.signatures(fMethod));
		} catch (Throwable e) {
			throw new InvocationTargetException(e);
		}
		if (runCount == 0)
			Assert
					.fail("Never found parameters that satisfied method.  Violated assumptions: "
							+ fInvalidParameters);
	}

	public boolean nullsOk() {
		Theory annotation= fMethod.getAnnotation(Theory.class);
		if (annotation == null)
			return false;
		return annotation.nullsAccepted();
	}

	int invokeWithActualParameters(Object target, Object[] params)
			throws Throwable {
		try {
			try {
				fMethod.invoke(target, params);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		} catch (AssumptionViolatedException e) {
			fInvalidParameters.add(e);
			return 0;
		} catch (Throwable e) {
			if (params.length == 0)
				throw e;
			throw new ParameterizedAssertionError(e, fMethod.getName(), params);
		}
		return 1;
	}

	int runWithDiscoveredParameterValues(Object target,
			PotentialMethodValues valueSources, List<ParameterSignature> sigs)
			throws Throwable {
		if (sigs.size() == 0) {
			try {
				return invokeWithActualParameters(target, valueSources
						.getValues(nullsOk()));
			} catch (CouldNotGenerateValueException e) {
				return 0;
			}
		}

		int count= 0;

		for (PotentialParameterValue source : sigs.get(0).getPotentialValues(
				target)) {
			count+= runWithDiscoveredParameterValues(target, valueSources
					.concat(source), sigs.subList(1, sigs.size()));
		}

		return count;
	}
}