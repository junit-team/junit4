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

	public Assignments(List<PotentialAssignment> assigned,
			List<ParameterSignature> unassigned, Class<?> type) {
		fUnassigned= unassigned;
		fAssigned= assigned;
		fClass= type;
	}

	// TODO: (Oct 12, 2007 10:27:59 AM) Do I need testClass?

	
	public static Assignments allUnassigned(Method testMethod,
			Class<?> testClass) {
		return new Assignments(new ArrayList<PotentialAssignment>(),
				ParameterSignature.signatures(testMethod), testClass);
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
		return new Assignments(assigned, fUnassigned.subList(1, fUnassigned
				.size()), fClass);
	}

	public Object[] getActualValues(boolean nullsOk, Object target)
			throws CouldNotGenerateValueException {
		Object[] values= new Object[fAssigned.size()];
		for (int i= 0; i < values.length; i++) {
			values[i]= fAssigned.get(i).getValue(target);
			if (values[i] == null && !nullsOk)
				throw new CouldNotGenerateValueException();
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
}