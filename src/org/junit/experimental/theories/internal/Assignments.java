/**
 * 
 */
package org.junit.experimental.theories.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.internal.runners.model.EachTestNotifier;

public class Assignments {
	private final EachTestNotifier fContext;

	private List<PotentialAssignment> fAssigned;

	private final List<ParameterSignature> fUnassigned;

	private Object fTarget;

	public Assignments(EachTestNotifier context,
			List<ParameterSignature> unassigned, Object target) {
		this(context, new ArrayList<PotentialAssignment>(), unassigned, target);
	}

	public Assignments(EachTestNotifier context,
			List<PotentialAssignment> assigned,
			List<ParameterSignature> unassigned, Object target) {
		fContext= context;
		fUnassigned= unassigned;
		fAssigned= assigned;
		fTarget= target;
	}

	public static Assignments allUnassigned(EachTestNotifier context,
			Method testMethod, Object target) {
		return new Assignments(context, ParameterSignature
				.signatures(testMethod), target);
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
		return new Assignments(fContext, assigned, fUnassigned.subList(
				1, fUnassigned.size()), fTarget);
	}

	public Object getTarget() {
		return fTarget;
	}

	public EachTestNotifier getContext() {
		return fContext;
	}

	public Object[] getActualValues(boolean nullsOk)
			throws CouldNotGenerateValueException {
		Object[] values= new Object[fAssigned.size()];
		for (int i= 0; i < values.length; i++) {
			values[i]= fAssigned.get(i).getValue(getTarget());
			if (values[i] == null && !nullsOk)
				throw new CouldNotGenerateValueException();
		}
		return values;
	}

	public List<PotentialAssignment> potentialsForNextUnassigned()
			throws InstantiationException, IllegalAccessException {
		return new AssignmentRequest(getTarget(), nextUnassigned())
				.getPotentialAssignments();
	}
}