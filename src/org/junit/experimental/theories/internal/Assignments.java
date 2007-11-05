/**
 * 
 */
package org.junit.experimental.theories.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;

public class Assignments {
	private List<PotentialAssignment> fAssigned;

	private final List<ParameterSignature> fUnassigned;

	private final Class<?> fClass;

	private final int fConstructorParameterCount;

	public Assignments(List<PotentialAssignment> assigned,
			List<ParameterSignature> unassigned, Class<?> type, int constructorParameterCount) {
		fUnassigned= unassigned;
		fAssigned= assigned;
		fClass= type;
		fConstructorParameterCount= constructorParameterCount;
	}

	// TODO: (Oct 12, 2007 10:27:59 AM) Do I need testClass?

	public static Assignments allUnassigned(Method testMethod,
			Class<?> testClass) {
		List<ParameterSignature> signatures= ParameterSignature.signatures(testClass.getConstructors()[0]);
		int constructorParameterCount = signatures.size();
		signatures.addAll(ParameterSignature.signatures(testMethod));
		return new Assignments(new ArrayList<PotentialAssignment>(),
				signatures, testClass, constructorParameterCount);
	}

	public boolean isComplete() {
		return fUnassigned.size() == 0;
	}

	public ParameterSignature nextUnassigned() {
		return fUnassigned.get(0);
	}

	public Assignments assignNext(PotentialAssignment source) {
		List<PotentialAssignment> assigned= new ArrayList<PotentialAssignment>(
				fAssigned);
		assigned.add(source);
		
		// TODO: (Nov 5, 2007 9:47:51 AM) pass-through

		return new Assignments(assigned, fUnassigned.subList(1, fUnassigned
				.size()), fClass, fConstructorParameterCount);
	}

	public Object[] getActualValues(boolean nullsOk, int start, int stop) throws CouldNotGenerateValueException {
		Object[] values= new Object[stop - start];
		for (int i= start; i < stop; i++) {
			Object value= fAssigned.get(i).getValue();
			if (value == null && !nullsOk)
				throw new CouldNotGenerateValueException();
			values[i - start]= value;
		}
		return values;
	}

	public List<PotentialAssignment> potentialsForNextUnassigned()
			throws InstantiationException, IllegalAccessException {
		ParameterSignature unassigned= nextUnassigned();
		return getSupplier(unassigned).getValueSources(unassigned);
	}

	public ParameterSupplier getSupplier(ParameterSignature unassigned)
			throws InstantiationException, IllegalAccessException {
		ParameterSupplier supplier= getAnnotatedSupplier(unassigned);
		if (supplier != null)
			return supplier;

		return new AllMembersSupplier(fClass);
	}

	public ParameterSupplier getAnnotatedSupplier(ParameterSignature unassigned)
			throws InstantiationException, IllegalAccessException {
		ParametersSuppliedBy annotation= unassigned
				.findDeepAnnotation(ParametersSuppliedBy.class);
		if (annotation == null)
			return null;
		return annotation.value().newInstance();
	}

	public Object[] getConstructorArguments(boolean nullsOk) throws CouldNotGenerateValueException {
		return getActualValues(nullsOk, 0, fConstructorParameterCount);
	}

	public Object[] getMethodArguments(boolean nullsOk, Object target) throws CouldNotGenerateValueException {
		return getActualValues(nullsOk, fConstructorParameterCount, fAssigned.size());
	}

	public Object[] getAllArguments(boolean nullsOk) throws CouldNotGenerateValueException {
		return getActualValues(nullsOk, 0, fAssigned.size());
	}
}