package org.junit.tests.experimental.theories.extendingwithstubs;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.internal.runners.model.InitializationError;
import org.junit.internal.runners.model.TestMethod;

public class StubbedTheories extends Theories {
	public StubbedTheories(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected TheoryAnchor invoke(TestMethod method, Object test) {
		return new StubbedTheoryAnchor(method);
	}
	
	public class StubbedTheoryAnchor extends TheoryAnchor {
		public StubbedTheoryAnchor(TestMethod method) {
			super(method);
		}

		private List<GuesserQueue> queues= new ArrayList<GuesserQueue>();

		@Override
		protected void handleAssumptionViolation(AssumptionViolatedException e) {
			super.handleAssumptionViolation(e);
			for (GuesserQueue queue : queues)
				queue.update(e);
		}

		@Override
		protected void runWithIncompleteAssignment(Assignments incomplete)
				throws InstantiationException, IllegalAccessException,
				Throwable {
			GuesserQueue guessers= createGuesserQueue(incomplete);
			queues.add(guessers);
			while (!guessers.isEmpty())
				runWithAssignment(incomplete.assignNext(guessers.remove(0)));
			queues.remove(guessers);
		}

		@SuppressWarnings("unchecked")
		private GuesserQueue createGuesserQueue(Assignments incomplete)
				throws InstantiationException, IllegalAccessException {
			ParameterSignature nextUnassigned= incomplete.nextUnassigned();

			if (nextUnassigned.hasAnnotation(Stub.class)) {
				GuesserQueue queue= new GuesserQueue();
				queue.add(new Guesser(nextUnassigned.getType()));
				return queue;
			}

			return GuesserQueue.forSingleValues(incomplete.potentialsForNextUnassigned());
		}
	}

}
