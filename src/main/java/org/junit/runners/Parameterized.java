package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.links.Statement;
import org.junit.internal.runners.model.FrameworkMethod;
import org.junit.internal.runners.model.TestClass;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * <p>
 * The custom runner <code>Parameterized</code> implements parameterized
 * tests. When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * </p>
 * 
 * For example, to test a Fibonacci function, write:
 * 
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class FibonacciTest {
 * 	&#064;Parameters
 * 	public static Collection&lt;Object[]&gt; data() {
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
	 * Annotation for a method which provides parameters to be injected into the test class constructor by <code>Parameterized</code>
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface Parameters {
	}
	
	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public Parameterized(Class<?> klass) throws Throwable {
		super(klass, runners(klass));
		validate();
	}
	
	private static class TestClassRunnerForParameters extends JUnit4ClassRunner {
		private final int fParameterSetNumber;

		private final List<Object[]> fParameterList;

		TestClassRunnerForParameters(Class<?> type,
				List<Object[]> parameterList, int i) throws InitializationError {
			super(type);
			fParameterList= parameterList;
			fParameterSetNumber= i;
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(
					computeParams());
		}

		private Object[] computeParams() throws Exception {
			try {
				return fParameterList.get(fParameterSetNumber);
			} catch (ClassCastException e) {
				throw new Exception(String.format(
						"%s.%s() must return a Collection of arrays.",
						getTestClass().getName(), getParametersMethod(
								getTestClass().getJavaClass()).getName()));
			}
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
		protected void collectInitializationErrors(List<Throwable> errors) {
			// do nothing: validated before.
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return runChildren(notifier);
		}
	}

	
	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		getTestClass().validateStaticMethods(errors);
		getTestClass().validateInstanceMethods(errors);
	}

	private static ArrayList<Runner> runners(Class<?> klass) throws Throwable {
		List<Object[]> parametersList = getParametersList(klass);
		ArrayList<Runner> runners= new ArrayList<Runner>();
		for (int i= 0; i < parametersList.size(); i++)
			runners.add(new TestClassRunnerForParameters(klass, parametersList,
					i));
		return runners;
	}

	@SuppressWarnings("unchecked")
	private static List<Object[]> getParametersList(Class<?> klass)
			throws Throwable {
		return (List<Object[]>) getParametersMethod(klass)
				.invokeExplosively(null);
	}

	private static FrameworkMethod getParametersMethod(Class<?> klass)
			throws Exception {
		List<FrameworkMethod> methods= new TestClass(klass)
				.getAnnotatedMethods(Parameters.class);
		for (FrameworkMethod each : methods) {
			int modifiers= each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class "
				+ klass.getName());
	}

}
