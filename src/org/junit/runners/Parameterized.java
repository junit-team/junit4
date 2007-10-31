package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.ParentRunner;
import org.junit.internal.runners.links.Statement;
import org.junit.internal.runners.model.InitializationError;
import org.junit.internal.runners.model.TestClass;
import org.junit.internal.runners.model.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
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
public class Parameterized extends ParentRunner<Integer> implements Filterable {
	static class TestClassRunnerForParameters extends JUnit4ClassRunner {
		private final Object[] fParameters;

		private final int fParameterSetNumber;

		private final Constructor<?> fConstructor;

		TestClassRunnerForParameters(Class<?> type, Object[] parameters, int i)
				throws InitializationError {
			super(type);
			fParameters= parameters;
			fParameterSetNumber= i;
			fConstructor= getOnlyConstructor();
		}

		@Override
		public Object createTest() throws Exception {
			return fConstructor.newInstance(fParameters);
		}

		@Override
		protected String getName() {
			return String.format("[%s]", fParameterSetNumber);
		}

		@Override
		protected String testName(final TestMethod method) {
			return String.format("%s[%s]", method.getName(),
					fParameterSetNumber);
		}

		private Constructor<?> getOnlyConstructor() {
			Constructor<?>[] constructors= getTestClass().getJavaClass()
					.getConstructors();
			Assert.assertEquals(1, constructors.length);
			return constructors[0];
		}

		@Override
		protected void collectInitializationErrors(List<Throwable> errors) {
			// do nothing: validated before.
		}

		@Override
		public void run(RunNotifier notifier) {
			for (TestMethod method : fTestMethods)
				runChild(method, notifier);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface Parameters {
	}

	private List<Object[]> fParameters;

	public Parameterized(Class<?> klass) throws Throwable {
		super(klass);
		fTestClass= new TestClass(klass);

		List<Throwable> errors= new ArrayList<Throwable>();
		fTestClass.validateStaticMethods(errors);
		fTestClass.validateInstanceMethods(errors);
		assertValid(errors);

		fParameters= getParametersList();
		for (final Object each : fParameters) {
			if (!(each instanceof Object[]))
				throw new Exception(String.format(
						"%s.%s() must return a Collection of arrays.",
						fTestClass.getName(), getParametersMethod().getName()));
		}
	}

	@Override
	protected Statement classBlock(final RunNotifier notifier) {
		return new Statement() {
			@Override
			public void evaluate() throws InitializationError {
				for (Integer i : getChildren()) {
					runChild(i, notifier);
				}
			}
		};
	}

	protected void runChild(Integer i, RunNotifier notifier) {
		try {
		new TestClassRunnerForParameters(fTestClass
				.getJavaClass(), fParameters.get(i), i++).run(notifier);
		} catch (Exception e) {
			// TODO: do something better here.
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList() throws Throwable {
		return (List<Object[]>) getParametersMethod().invokeExplosively(
				null);
	}

	private TestMethod getParametersMethod() throws Exception {
		List<TestMethod> methods= fTestClass
				.getAnnotatedMethods(Parameters.class);
		for (TestMethod each : methods) {
			int modifiers= each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class "
				+ fTestClass.getName());
	}

	public static Collection<Object[]> eachOne(Object... params) {
		List<Object[]> results= new ArrayList<Object[]>();
		for (Object param : params)
			results.add(new Object[] { param });
		return results;
	}

	@Override
	protected List<Integer> getChildren() {
		// TODO: (Oct 29, 2007 1:57:23 PM) Should this be Iterable?  Iterator?

		ArrayList<Integer> ints= new ArrayList<Integer>();
		for (int i = 0; i < fParameters.size(); i++) {
			ints.add(i);
		}
		return ints;
	}

	@Override
	protected Description describeChild(Integer i) {
		try {
			return new TestClassRunnerForParameters(
					fTestClass.getJavaClass(), null, i++).getDescription();
		} catch (InitializationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		int i= 0;
		Iterator<Object[]> iterator= fParameters.iterator();
		while (iterator.hasNext()) {
			try {
				if (!filter.shouldRun(new TestClassRunnerForParameters(
							fTestClass.getJavaClass(), iterator.next(), i++).getDescription()))
					iterator.remove();
			} catch (InitializationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
