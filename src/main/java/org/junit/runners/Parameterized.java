package org.junit.runners;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * <p>
 * The custom runner <code>Parameterized</code> implements parameterized tests.
 * When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * </p>
 * 
 * For example, to test a Fibonacci function, write:
 * 
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class FibonacciTest {
 * 	&#064;Parameters
 * 	public static Iterable&lt;Object[]&gt; data() {
 * 		return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },
 * 				{ 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
 * 	}
 * 
 * 	private int fInput;
 * 
 * 	private int fExpected;
 * 
 * 	public FibonacciTest(int input, int expected) {
 * 		fInput= input;
 * 		fExpected= expected;
 * 	}
 * 
 * 	&#064;Test
 * 	public void test() {
 * 		assertEquals(fExpected, Fibonacci.compute(fInput));
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * Each instance of <code>FibonacciTest</code> will be constructed using the
 * two-argument constructor and the data values in the
 * <code>&#064;Parameters</code> method.
 * </p>
 */
public class Parameterized extends Suite {
	/**
	 * Annotation for a method which provides parameters to be injected into the
	 * test class constructor by <code>Parameterized</code>
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface Parameters {
	}

	private class TestClassRunnerForParameters extends
			BlockJUnit4ClassRunner {
		private final int fParameterSetNumber;

		private final Object[] fParameters;

		TestClassRunnerForParameters(Class<?> type, Object[] parameters, int i)
				throws InitializationError {
			super(type);
			fParameters= parameters;
			fParameterSetNumber= i;
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(fParameters);
		}

		@Override
		protected String getName() {
			return String.format("[%s]", fParameterSetNumber);
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(),
					fParameterSetNumber);
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
		
		@Override
		protected Annotation[] getRunnerAnnotations() {
			return new Annotation[0];
		}
	}

	private final ArrayList<Runner> runners= new ArrayList<Runner>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public Parameterized(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner> emptyList());
		Iterable<Object[]> allParameters= getAllParameters();
		createRunnersForParameters(allParameters);
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object[]> getAllParameters()
			throws Throwable {
		Object parameters= getParametersMethod().invokeExplosively(null);
		if (parameters instanceof Iterable)
			return (Iterable<Object[]>) parameters;
		else
			throw parametersMethodReturnedWrongType();
	}

	private FrameworkMethod getParametersMethod()
			throws Exception {
		List<FrameworkMethod> methods= getTestClass()
				.getAnnotatedMethods(Parameters.class);
		for (FrameworkMethod each : methods) {
			if (each.isStatic() && each.isPublic())
				return each;
		}

		throw new Exception("No public static parameters method on class "
				+ getTestClass().getName());
	}

	private void createRunnersForParameters(Iterable<Object[]> allParameters)
			throws InitializationError, Exception {
		try {
			int i= 0;
			for (Object[] parametersOfSingleTest : allParameters) {
				TestClassRunnerForParameters runner= new TestClassRunnerForParameters(
						getTestClass().getJavaClass(), parametersOfSingleTest,
						i);
				runners.add(runner);
				++i;
			}
		} catch (ClassCastException e) {
			throw parametersMethodReturnedWrongType();
		}
	}

	private Exception parametersMethodReturnedWrongType() throws Exception {
		String className= getTestClass().getName();
		String methodName= getParametersMethod().getName();
		String message= format("%s.%s() must return an Iterable of arrays.",
				className, methodName);
		return new Exception(message);
	}
}
