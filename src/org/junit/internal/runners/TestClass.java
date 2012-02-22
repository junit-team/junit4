package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class TestClass {
	private final Class<?> fClass;
	
	public TestClass(Class<?> klass) {
		fClass= klass;
	}

	public List<Method> getTestMethods() {
		return getAnnotatedMethods(Test.class);
	}

	List<Method> getBefores() {
		return getAnnotatedMethods(BeforeClass.class);
	}

	List<Method> getAfters() {
		return getAnnotatedMethods(AfterClass.class);
	}
	
	public List<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass) {
		List<Method> results= new ArrayList<Method>();
		for (Class<?> eachClass : getSuperClasses(fClass)) {
			Method[] methods= eachClass.getDeclaredMethods();
			for (Method eachMethod : methods) {
				Annotation annotation= eachMethod.getAnnotation(annotationClass);
				if (annotation != null && ! isShadowed(eachMethod, results)) 
					results.add(eachMethod);
			}
		}
		if (runsTopToBottom(annotationClass))
			Collections.reverse(results);
		return results;
	}

	private boolean runsTopToBottom(Class< ? extends Annotation> annotation) {
		return annotation.equals(Before.class) || annotation.equals(BeforeClass.class);
	}
	
	private boolean isShadowed(Method method, List<Method> results) {
		for (Method each : results) {
			if (isShadowed(method, each))
				return true;
		}
		return false;
	}

	private boolean isShadowed(Method current, Method previous) {
		if (! previous.getName().equals(current.getName()))
			return false;
		if (previous.getParameterTypes().length != current.getParameterTypes().length)
			return false;
		for (int i= 0; i < previous.getParameterTypes().length; i++) {
			if (! previous.getParameterTypes()[i].equals(current.getParameterTypes()[i]))
				return false;
		}
		return true;
	}

	private List<Class<?>> getSuperClasses(Class< ?> testClass) {
		ArrayList<Class<?>> results= new ArrayList<Class<?>>();
		Class<?> current= testClass;
		while (current != null) {
			results.add(current);
			current= current.getSuperclass();
		}
		return results;
	}

	public Constructor<?> getConstructor() throws SecurityException, NoSuchMethodException {
		return fClass.getConstructor();
	}

	public Class<?> getJavaClass() {
		return fClass;
	}

	public String getName() {
		return fClass.getName();
	}

}
