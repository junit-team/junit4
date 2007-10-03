/**
 * 
 */
package org.junit.tests.experimental.theories.extendingwithstubs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.experimental.theories.internal.TheoryMethodRunner;
import org.junit.internal.runners.TestClass;

public class StubbedTheoryMethod extends TheoryMethodRunner {
	private List<GuesserQueue> queues= new ArrayList<GuesserQueue>();

	public StubbedTheoryMethod(Method method, TestClass testClass) {
		super(method, testClass);
	}

	@Override
	protected void handleAssumptionViolation(AssumptionViolatedException e) {
		super.handleAssumptionViolation(e);
		for (GuesserQueue queue : queues) {
			queue.update(e);
		}
	}

	@Override
	protected void runWithIncompleteAssignment(Assignments incomplete)
			throws InstantiationException, IllegalAccessException, Throwable {
		GuesserQueue guessers= createGuesserQueue(incomplete);
		queues.add(guessers);
		while (!guessers.isEmpty()) {
			runWithAssignment(incomplete.assignNext(guessers.remove(0)));
		}
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
