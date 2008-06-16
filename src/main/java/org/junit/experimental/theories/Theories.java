/**
 * 
 */
package org.junit.experimental.theories;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.BlockJUnit4ClassRunner;
import org.junit.internal.runners.links.Statement;
import org.junit.internal.runners.model.FrameworkMethod;

public class Theories extends BlockJUnit4ClassRunner {
	public Theories(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		Field[] fields= getTestClass().getJavaClass().getDeclaredFields();
		
		for (Field each : fields)
			if (each.getAnnotation(DataPoint.class) != null && !Modifier.isStatic(each.getModifiers()))
				errors.add(new Error("DataPoint field " + each.getName() + " must be static"));
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> testMethods= super.computeTestMethods();
		List<FrameworkMethod> theoryMethods= getTestClass().getAnnotatedMethods(Theory.class);
		testMethods.removeAll(theoryMethods);
		testMethods.addAll(theoryMethods);
		return testMethods;
	}

	@Override
	public Statement childBlock(final FrameworkMethod method) {
		return new TheoryAnchor(method);
	}

	public class TheoryAnchor extends Statement {
		private int successes= 0;

		private FrameworkMethod fTestMethod;

		private List<AssumptionViolatedException> fInvalidParameters= new ArrayList<AssumptionViolatedException>();

		public TheoryAnchor(FrameworkMethod method) {
			fTestMethod= method;
		}

		@Override
		public void evaluate() throws Throwable {
			runWithAssignment(Assignments.allUnassigned(
					fTestMethod.getMethod(), getTestClass()));

			if (successes == 0)
				Assert
						.fail("Never found parameters that satisfied method assumptions.  Violated assumptions: "
								+ fInvalidParameters);
		}

		protected void runWithAssignment(Assignments parameterAssignment)
				throws Throwable {
			if (!parameterAssignment.isComplete()) {
				runWithIncompleteAssignment(parameterAssignment);
			} else {
				runWithCompleteAssignment(parameterAssignment);
			}
		}

		protected void runWithIncompleteAssignment(Assignments incomplete)
				throws InstantiationException, IllegalAccessException,
				Throwable {
			for (PotentialAssignment source : incomplete
					.potentialsForNextUnassigned()) {
				runWithAssignment(incomplete.assignNext(source));
			}
		}

		protected void runWithCompleteAssignment(final Assignments complete)
				throws InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException, Throwable {
			new BlockJUnit4ClassRunner(getTestClass()) {
				@Override
				protected void collectInitializationErrors(
						List<Throwable> errors) {
					// do nothing
				}

				@Override
				public Statement childBlock(FrameworkMethod method) {
					final Statement statement= super.childBlock(method);
					return new Statement() {
						@Override
						public void evaluate() throws Throwable {
							try {
								statement.evaluate();
								handleDataPointSuccess();
							} catch (AssumptionViolatedException e) {
								handleAssumptionViolation(e);
							} catch (Throwable e) {
								reportParameterizedError(e, complete
										.getArgumentStrings(nullsOk()));
							}
						}

					};
				}

				@Override
				protected Statement invoke(FrameworkMethod method, Object test) {
					return methodCompletesWithParameters(method, complete, test);
				}

				@Override
				public Object createTest() throws Exception {
					return getTestClass().getConstructor().newInstance(
							complete.getConstructorArguments(nullsOk()));
				}
			}.childBlock(fTestMethod).evaluate();
		}

		private Statement methodCompletesWithParameters(
				final FrameworkMethod method, final Assignments complete, final Object freshInstance) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					try {
						final Object[] values= complete.getMethodArguments(
								nullsOk());
						method.invokeExplosively(freshInstance, values);
					} catch (CouldNotGenerateValueException e) {
						// ignore
					}
				}
			};
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

		protected void handleDataPointSuccess() {
			successes++;
		}
	}
}