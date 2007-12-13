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
	private static class TestClassRunnerForParameters extends JUnit4ClassRunner {
		private final int fParameterSetNumber;
		private final List<Object[]> fParameterList;

		TestClassRunnerForParameters(Class<?> type, List<Object[]> parameterList, int i)
				throws InitializationError {
			super(type);
			fParameterList= parameterList;
			fParameterSetNumber= i;
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(computeParams());
		}

		private Object[] computeParams() throws Exception {
			try {
				return fParameterList.get(fParameterSetNumber);
			} catch (ClassCastException e) {
				throw new Exception(String.format(
						"%s.%s() must return a Collection of arrays.",
						getTestClass().getName(), getParametersMethod(fTestClass).getName()));
				// TODO: (Dec 10, 2007 9:22:07 PM) Should fTestClass be protected?

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

	public Parameterized(Class<?> klass) throws Throwable {
		// TODO: (Dec 11, 2007 10:06:16 PM) is this the only call?
		this(klass, getParametersList(new TestClass(klass)));
	}
	
	// TODO: (Dec 11, 2007 10:09:48 PM) Parameterized so desperately wants to be a ParentRunner

	private Parameterized(Class<?> klass, List<Object[]> parametersList) throws InitializationError {
		super(klass, runners(klass, parametersList));

		List<Throwable> errors= new ArrayList<Throwable>();
		new TestClass(klass).validateStaticMethods(errors);
		new TestClass(klass).validateInstanceMethods(errors);
		assertValid(errors);
	}

	private static ArrayList<Runner> runners(Class<?> klass,
			List<Object[]> parametersList) throws InitializationError {
		ArrayList<Runner> runners= new ArrayList<Runner>();
		for (int i = 0; i < parametersList.size(); i++)
			// TODO: (Dec 11, 2007 10:08:16 PM) pass-through
			runners.add(new TestClassRunnerForParameters(klass, parametersList, i));
		return runners;
	}

	@SuppressWarnings("unchecked")
	private static List<Object[]> getParametersList(TestClass testClass) throws Throwable {
		return (List<Object[]>) getParametersMethod(testClass).invokeExplosively(
				null);
	}

	private static FrameworkMethod getParametersMethod(TestClass testClass) throws Exception {
		List<FrameworkMethod> methods= testClass
				.getAnnotatedMethods(Parameters.class);
		for (FrameworkMethod each : methods) {
			int modifiers= each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class "
				+ testClass.getName());
	}

	public static List<Object[]> eachOne(Object... params) {
		List<Object[]> results= new ArrayList<Object[]>();
		for (Object param : params)
			results.add(new Object[] { param });
		return results;
	}
}
