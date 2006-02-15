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

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestClassRunner;

public class Parameterized extends TestClassRunner {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface Parameters {
	}

	public static Collection<Object[]> eachOne(Object... params) {
		ArrayList<Object[]> returnThis= new ArrayList<Object[]>();
		for (Object param : params) {
			returnThis.add(new Object[] { param });
		}
		return returnThis;
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
