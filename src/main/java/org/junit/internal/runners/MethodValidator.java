package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * @deprecated Included for backwards compatibility with JUnit 4.4. Will be
 *             removed in the next release. Please use
 *             {@link BlockJUnit4ClassRunner} in place of {@link JUnit4ClassRunner}.
 */
@Deprecated
public class MethodValidator {

	private final List<Throwable> fErrors= new ArrayList<Throwable>();

	private TestClass fTestClass;

	public MethodValidator(TestClass testClass) {
		fTestClass = testClass;
	}

	public void validateInstanceMethods() {
		validateTestMethods(After.class, false);
		validateTestMethods(Before.class, false);
		validateTestMethods(Test.class, false);
		
		List<Method> methods= fTestClass.getAnnotatedMethods(Test.class);
		if (methods.size() == 0)
			fErrors.add(new Exception("No runnable methods"));
	}

	public void validateStaticMethods() {
		validateTestMethods(BeforeClass.class, true);
		validateTestMethods(AfterClass.class, true);
	}
	
	public List<Throwable> validateMethodsForDefaultRunner() {
		validateNoArgConstructor();
		validateStaticMethods();
		validateInstanceMethods();
		return fErrors;
	}
	
	public void assertValid() throws InitializationError {
		if (!fErrors.isEmpty())
			throw new InitializationError(fErrors);
	}

	public void validateNoArgConstructor() {
		try {
			fTestClass.getConstructor();
		} catch (Exception e) {
			fErrors.add(new Exception("Test class should have public zero-argument constructor", e));
		}
	}

	private void validateTestMethods(Class<? extends Annotation> annotation,
			boolean isStatic) {
		List<Method> methods= fTestClass.getAnnotatedMethods(annotation);
		
		for (Method each : methods) {
			if (Modifier.isStatic(each.getModifiers()) != isStatic) {
				String state= isStatic ? "should" : "should not";
				fErrors.add(new Exception("Method " + each.getName() + "() "
						+ state + " be static"));
			}
			if (!Modifier.isPublic(each.getDeclaringClass().getModifiers()))
				fErrors.add(new Exception("Class " + each.getDeclaringClass().getName()
						+ " should be public"));
			if (!Modifier.isPublic(each.getModifiers()))
				fErrors.add(new Exception("Method " + each.getName()
						+ " should be public"));
			if (each.getReturnType() != Void.TYPE)
				fErrors.add(new Exception("Method " + each.getName()
						+ " should be void"));
			if (each.getParameterTypes().length != 0)
				fErrors.add(new Exception("Method " + each.getName()
						+ " should have no parameters"));
		}
	}
}
