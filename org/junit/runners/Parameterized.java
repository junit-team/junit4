package org.junit.runners;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestClassRunner;

/** The custom runner <code>Parameterized</code> implements parameterized
 * tests. When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.<br>
 * <p>
 * For example, to test a Fibonacci function, write:
 * <code>
 * &nbsp;<br>@RunWith(Parameterized.class)<br>
 * public class FibonacciTest {<br>
 * &nbsp;&nbsp;@Parameters<br>
 * &nbsp;&nbsp;public static Collection<Object[]> data() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{ 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });<br>
 * &nbsp;&nbsp;}<br>
 *<br>
 * &nbsp;&nbsp;private int fInput;<br>
 * &nbsp;&nbsp;private int fExpected;<br>
 *<br>
 * &nbsp;&nbsp;public FibonacciTest(int input, int expected) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;fInput= input;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;fExpected= expected;<br>
 * &nbsp;&nbsp;}<br>
 *<br>
 * &nbsp;&nbsp;@Test public void test() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;assertEquals(fExpected, Fibonacci.compute(fInput));<br>
 * &nbsp;&nbsp;}<br>
 * }<br>
 * </code>
 * <p>
 * Each instance of <code>FibonacciTest</code> will be constructed using the two-argument
 * constructor and the data values in the <code>@Parameters</code> method.
 */
public class Parameterized extends TestClassRunner {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface Parameters {
	}

	public static Collection<Object[]> eachOne(Object... params) {
		List<Object[]> results= new ArrayList<Object[]>();
		for (Object param : params)
			results.add(new Object[] { param });
		return results;
	}

	// TODO: single-class this extension
	
	private static class TestClassRunnerForParameters extends TestClassMethodsRunner {
		private final Object[] fParameters;

		private final int fParameterSetNumber;

		private final Constructor fConstructor;

		private TestClassRunnerForParameters(Class<?> klass, Object[] parameters, int i) {
			super(klass);
			fParameters= parameters;
			fParameterSetNumber= i;
			fConstructor= getOnlyConstructor();
		}

		@Override
		protected Object createTest() throws Exception {
			return fConstructor.newInstance(fParameters);
		}
		
		@Override
		protected String getName() {
			return String.format("[%s]", fParameterSetNumber);
		}
		
		@Override
		protected String testName(final Method method) {
			return String.format("%s[%s]", method.getName(), fParameterSetNumber);
		}

		private Constructor getOnlyConstructor() {
			Constructor[] constructors= getTestClass().getConstructors();
			assertEquals(1, constructors.length);
			return constructors[0];
		}
	}
	
	// TODO: I think this now eagerly reads parameters, which was never the point.
	
	public static class RunAllParameterMethods extends CompositeRunner {
		private final Class<?> fKlass;

		public RunAllParameterMethods(Class<?> klass) throws Exception {
			super(klass.getName());
			fKlass= klass;
			int i= 0;
			for (final Object[] parameters : getParametersList()) {
				super.add(new TestClassRunnerForParameters(klass, parameters, i++));
			}
		}

		@SuppressWarnings("unchecked")
		private Collection<Object[]> getParametersList() throws IllegalAccessException, InvocationTargetException, Exception {
			return (Collection) getParametersMethod().invoke(null);
		}
		
		private Method getParametersMethod() throws Exception {
			for (Method each : fKlass.getMethods()) {
				if (Modifier.isStatic(each.getModifiers())) {
					Annotation[] annotations= each.getAnnotations();
					for (Annotation annotation : annotations) {
						if (annotation.annotationType() == Parameters.class)
							return each;
					}
				}
			}
			throw new Exception("No public static parameters method on class "
					+ getName());
		}
	}
	
	public Parameterized(final Class<?> klass) throws Exception {
		super(klass, new RunAllParameterMethods(klass));
	}
	
	@Override
	protected void validate(MethodValidator methodValidator) {
		methodValidator.validateStaticMethods();
		methodValidator.validateInstanceMethods();
	}
}
