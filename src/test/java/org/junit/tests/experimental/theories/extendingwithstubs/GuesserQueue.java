package org.junit.tests.experimental.theories.extendingwithstubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.experimental.theories.PotentialAssignment;
import org.junit.internal.AssumptionViolatedException;

public class GuesserQueue extends ArrayList<ReguessableValue> {
	static class ReguessableDecorator extends ReguessableValue {
		private final PotentialAssignment delegate;
	
		public ReguessableDecorator(PotentialAssignment delegate) {
			this.delegate= delegate;
		}
	
		@Override
		public List<ReguessableValue> reguesses(AssumptionViolatedException e) {
			return Collections.emptyList();
		}
	
		@Override
		public Object getValue() throws CouldNotGenerateValueException {
			return delegate.getValue();
		}

		@Override
		public String getDescription() throws CouldNotGenerateValueException {
			return delegate.getDescription();
		}
	}

	static GuesserQueue forSingleValues(
			List<PotentialAssignment> potentials) {
		GuesserQueue returnThis= new GuesserQueue();
		for (PotentialAssignment potentialParameterValue : potentials) {
			returnThis
					.add(new GuesserQueue.ReguessableDecorator(potentialParameterValue));
		}
		return returnThis;
	}

	private static final long serialVersionUID = 1L;
	private ReguessableValue lastRemoved;

	public void update(AssumptionViolatedException e) {
		if (lastRemoved != null)
			addAll(lastRemoved.reguesses(e));
	}
	
	@Override
	public ReguessableValue remove(int index) {
		lastRemoved = super.remove(index);
		return lastRemoved;
	}
}
