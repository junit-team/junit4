/**
 * 
 */
package org.junit.experimental.theories;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.links.Link;
import org.junit.internal.runners.links.WithBeforeAndAfter;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.InitializationError;
import org.junit.internal.runners.model.TestMethod;

@SuppressWarnings("restriction")
public class Theories extends JUnit4ClassRunner {
	public Theories(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
	}

	@Override
	protected List<TestMethod> getTestMethods() {
		// TODO: (Jul 20, 2007 2:02:44 PM) Only get methods once, even if they have both @Test and @Theory

		List<TestMethod> testMethods= super.getTestMethods();
		testMethods.addAll(getTestClass().getAnnotatedMethods(Theory.class));
		return testMethods;
	}

	@Override
	protected Link chain(final TestMethod method, Object test, EachTestNotifier notifier) {
		Link next= invoke(method, test);
		next= ignoreViolatedAssumptions(next);
		next= possiblyExpectingExceptions(method, next);
		return notifying(method, next, notifier);
	}

	@Override
	protected TheoryAnchor invoke(TestMethod method, Object test) {
		return new TheoryAnchor(method);
	}

	public class TheoryAnchor extends Link {
		private int successes = 0;
		private TestMethod fTestMethod;
		private List<AssumptionViolatedException> fInvalidParameters= new ArrayList<AssumptionViolatedException>();

		public TheoryAnchor(TestMethod method) {
			fTestMethod= method;
		}
		
		@Override
		public void run(FailureListener listener) {
			try {
				runWithAssignment(Assignments.allUnassigned(fTestMethod
						.getMethod(), fTestMethod.getTestClass().getJavaClass()), listener);
			} catch (Throwable e) {
				listener.addFailure(e);
			}
			
			if (!listener.failureSeen() && successes == 0)
				listener.addFailure(new AssertionError(
						"Never found parameters that satisfied method.  Violated assumptions: "
								+ fInvalidParameters));
		}

		protected void runWithAssignment(Assignments parameterAssignment, FailureListener listener)
				throws Throwable {
			// TODO: (Oct 9, 2007 8:56:54 PM) Should this be moved to Assignments?

			if (!parameterAssignment.isComplete()) {
				runWithIncompleteAssignment(parameterAssignment, listener);
			} else {
				runWithCompleteAssignment(parameterAssignment, listener);
			}
		}

		protected void runWithIncompleteAssignment(Assignments incomplete, FailureListener listener)
				throws InstantiationException, IllegalAccessException,
				Throwable {
			List<PotentialAssignment> potentialsForNextUnassigned= incomplete
							.potentialsForNextUnassigned();
			for (PotentialAssignment source : potentialsForNextUnassigned) {
				runWithAssignment(incomplete.assignNext(source), listener);
			}
		}

		protected void runWithCompleteAssignment(final Assignments complete, final FailureListener listener)
				throws Throwable {
			final Object freshInstance= createTest();
			new WithBeforeAndAfter(new Link() {
				@Override
				public void run(FailureListener listener) {
					try {
						invokeWithActualParameters(freshInstance, complete);
					} catch (Throwable e) {
						listener.addFailure(e);
					}
				}
			}, fTestMethod, freshInstance).run(new FailureListener() {
				@Override
				protected void handleFailure(Throwable error) {
					if (!(error instanceof CouldNotGenerateValueException))
						listener.addFailure(error);
				}
			}); 
		}

		private void invokeWithActualParameters(Object target, Assignments complete)
				throws Throwable {
			final Object[] values= complete.getActualValues(nullsOk(), target);
			try {
				fTestMethod.invokeExplosively(target, values);
				successes++;
			} catch (AssumptionViolatedException e) {
				handleAssumptionViolation(e);
			} catch (Throwable e) {
				reportParameterizedError(e, values);
			}
		}

		protected void handleAssumptionViolation(AssumptionViolatedException e) {
			fInvalidParameters.add(e);
		}

		protected void reportParameterizedError(Throwable e, Object... params)
				throws Throwable {
			if (params.length == 0)
				throw e;
			throw new ParameterizedAssertionError(e, fTestMethod.getName(),
					params);
		}

		private boolean nullsOk() {
			Theory annotation= fTestMethod.getMethod().getAnnotation(
					Theory.class);
			if (annotation == null)
				return false;
			return annotation.nullsAccepted();
		}
	}
}