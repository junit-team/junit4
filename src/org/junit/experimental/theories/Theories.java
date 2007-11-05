/**
 * 
 */
package org.junit.experimental.theories;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.links.Statement;
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
	protected List<TestMethod> computeTestMethods() {
		// TODO: (Jul 20, 2007 2:02:44 PM) Only get methods once, even if they
		// have both @Test and @Theory

		List<TestMethod> testMethods= super.computeTestMethods();
		testMethods.addAll(getTestClass().getAnnotatedMethods(Theory.class));
		return testMethods;
	}

	@Override
	public Statement childBlock(final TestMethod method) {
		return new TheoryAnchor(method);
	}

	public class TheoryAnchor extends Statement {
		private int successes= 0;

		private TestMethod fTestMethod;

		private List<AssumptionViolatedException> fInvalidParameters= new ArrayList<AssumptionViolatedException>();

		public TheoryAnchor(TestMethod method) {
			fTestMethod= method;
		}

		@Override
		public void evaluate() throws Throwable {
			runWithAssignment(Assignments.allUnassigned(
					fTestMethod.getMethod(), fTestMethod.getTestClass()
							.getJavaClass()));

			if (successes == 0)
				Assert
						.fail("Never found parameters that satisfied method.  Violated assumptions: "
								+ fInvalidParameters);
		}

		protected void runWithAssignment(Assignments parameterAssignment) throws Throwable {
			if (!parameterAssignment.isComplete()) {
				runWithIncompleteAssignment(parameterAssignment);
			} else {
				runWithCompleteAssignment(parameterAssignment);
			}
		}

		protected void runWithIncompleteAssignment(Assignments incomplete) throws InstantiationException,
				IllegalAccessException, Throwable {
			for (PotentialAssignment source : incomplete
					.potentialsForNextUnassigned()) {
				runWithAssignment(incomplete.assignNext(source));
			}
		}

		protected void runWithCompleteAssignment(final Assignments complete) throws InstantiationException,
				IllegalAccessException, InvocationTargetException,
				NoSuchMethodException, Throwable {
			try {
				new JUnit4ClassRunner(getTestClass().getJavaClass()) {
					@Override
					protected void collectInitializationErrors(
							List<Throwable> errors) {
						// TODO: (Oct 12, 2007 12:08:03 PM) DUP
						// do nothing
					}
					
					@Override
					public Statement childBlock(TestMethod method) {
						// TODO: (Oct 12, 2007 2:00:52 PM) Name this Link
						final Statement link= super.childBlock(method);
						return new Statement() {
						
							@Override
							public void evaluate() throws Throwable {
								try {
									link.evaluate();
									successes++;
								} catch (AssumptionViolatedException e) {
									// TODO: (Oct 12, 2007 2:07:01 PM) DUP? even correct?
									// do nothing
								} catch (Throwable e) {
									 // TODO: (Oct 12, 2007 2:04:01 PM) nullsOk as argument to Assignments constructor

										reportParameterizedError(e, complete.getAllArguments(nullsOk()));
									}
							}
						
						};
					}
					
					@Override
					protected Statement invoke(TestMethod method, Object test) {
						// TODO: (Oct 12, 2007 12:07:28 PM) push method in
						return methodCompletesWithParameters(complete, test);
					}
					
					@Override
					protected Statement ignoreViolatedAssumptions(final Statement next) {
						// TODO: (Oct 12, 2007 2:15:02 PM) name this

						return new Statement() {
						
							@Override
							public void evaluate() throws Throwable {
								try {
									next.evaluate();						
								} catch (AssumptionViolatedException e) {
									// TODO: (Oct 12, 2007 2:19:52 PM) This feels hacky

									successes--;
									handleAssumptionViolation(e);
									// TODO: (Oct 12, 2007 2:15:44 PM) Can I remove other calls?

								}
							}
						};
					}
					
					@Override
					public Object createTest() throws Exception {
						// TODO: (Oct 12, 2007 12:31:12 PM) DUP
						// TODO: (Oct 12, 2007 12:40:33 PM) honor assumption violations in JUnit4ClassRunner constructor invocations

						return getTestClass().getJavaClass().getConstructors()[0].newInstance(complete.getConstructorArguments(nullsOk()));
					}
				}.childBlock(fTestMethod).evaluate();
			} catch (AssumptionViolatedException e) {
				handleAssumptionViolation(e);
			} catch (CouldNotGenerateValueException e) {
				// Do nothing
			}
		}

		private Statement methodCompletesWithParameters(final Assignments complete,
				final Object freshInstance) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					try {
						invokeWithActualParameters(freshInstance, complete);
					} catch (CouldNotGenerateValueException e) {
						// ignore 
						// TODO: (Oct 12, 2007 9:58:11 AM) Do I ignore this elsewhere?
					}
				}
			};
		}

		private void invokeWithActualParameters(Object target,
				Assignments complete) throws Throwable {
			final Object[] values= complete.getMethodArguments(nullsOk(), target);
//			try {
				fTestMethod.invokeExplosively(target, values);
//			} catch (AssumptionViolatedException e) {
//				handleAssumptionViolation(e);
//			}
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