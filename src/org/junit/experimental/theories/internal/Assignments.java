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
import org.junit.internal.runners.Roadie;

public class Assignments {
	private final Roadie fContext;

	private List<PotentialAssignment> fAssigned;

	private final List<ParameterSignature> fUnassigned;

	public Assignments(Roadie context,
			List<ParameterSignature> unassigned) {
		this(context, new ArrayList<PotentialAssignment>(), unassigned);
	}

	public Assignments(Roadie context,
			List<PotentialAssignment> assigned,
			List<ParameterSignature> unassigned) {
		fContext= context;
		fUnassigned= unassigned;
		fAssigned= assigned;
	}

	public static Assignments allUnassigned(Roadie context,
			Method method) {
		return new Assignments(context, ParameterSignature
				.signatures(method));
	}

	boolean isComplete() {
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
				1, fUnassigned.size()));
	}

	public Object getTarget() {
		return fContext.getTarget();
	}

	public Roadie getContext() {
		return fContext;
	}

	public Object[] getActualValues(boolean nullsOk)
			throws CouldNotGenerateValueException {
		Object[] values= new Object[fAssigned.size()];
		for (int i= 0; i < values.length; i++) {
			values[i]= fAssigned.get(i).getValue();
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