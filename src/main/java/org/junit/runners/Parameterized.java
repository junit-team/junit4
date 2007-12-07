package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.links.Statement;
import org.junit.internal.runners.model.FrameworkMethod;
import org.junit.runner.manipulation.Filterable;
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
public class Parameterized extends CompositeRunner implements Filterable {
	private class TestClassRunnerForParameters extends JUnit4ClassRunner {
		private final int fParameterSetNumber;

		TestClassRunnerForParameters(Class<?> type, int i)
				throws InitializationError {
			super(type);
			fParameterSetNumber= i;
		}

		@Override
		public Object createTest() throws Exception {
			return fConstructor.newInstance(computeParams());
		}

		private Object[] computeParams() throws Exception {
			try {
				return fParameters.get(fParameterSetNumber);
			} catch (ClassCastException e) {
				throw new Exception(String.format(
						"%s.%s() must return a Collection of arrays.",
						getTestClass().getName(), getParametersMethod().getName()));				
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

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface Parameters {
	}

	private List<Object[]> fParameters;
	
	private Constructor<?> fConstructor;

	public Parameterized(Class<?> klass) throws Throwable {
		super(klass, klass.getName());

		List<Throwable> errors= new ArrayList<Throwable>();
		getTestClass().validateStaticMethods(errors);
		getTestClass().validateInstanceMethods(errors);
		assertValid(errors);

		fParameters= getParametersList();

		for (int i = 0; i < fParameters.size(); i++)
			add(new TestClassRunnerForParameters(klass, i));
		
		fConstructor= getTestClass().getOnlyConstructor();
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList() throws Throwable {
		return (List<Object[]>) getParametersMethod().invokeExplosively(
				null);
	}

	private FrameworkMethod getParametersMethod() throws Exception {
		List<FrameworkMethod> methods= getTestClass()
				.getAnnotatedMethods(Parameters.class);
		for (FrameworkMethod each : methods) {
			int modifiers= each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class "
				+ getTestClass().getName());
	}

	public static List<Object[]> eachOne(Object... params) {
		List<Object[]> results= new ArrayList<Object[]>();
		for (Object param : params)
			results.add(new Object[] { param });
		return results;
	}
}
