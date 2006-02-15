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

public class MethodValidator {
	private final TestIntrospector fIntrospector;

	private final List<Throwable> fErrors= new ArrayList<Throwable>();

	private final Class<?> fTestClass;

	public MethodValidator(Class<?> testClass) {
		fTestClass= testClass;
		fIntrospector= new TestIntrospector(testClass);
	}

	public void validateInstanceMethods() {
		validateTestMethods(After.class, false);
		validateTestMethods(Before.class, false);
		validateTestMethods(Test.class, false);
	}

	public void validateStaticMethods() {
		validateTestMethods(BeforeClass.class, true);
		validateTestMethods(AfterClass.class, true);
	}
	
	public List<Throwable> validateAllMethods() {
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
		List<Method> methods= fIntrospector.getTestMethods(annotation);
		for (Method each : methods) {
			if (Modifier.isStatic(each.getModifiers()) != isStatic) {
				String state= isStatic ? "should" : "should not";
				fErrors.add(new Exception("Method " + each.getName() + "() "
						+ state + " be static"));
			}
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
