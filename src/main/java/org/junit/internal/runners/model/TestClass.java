package org.junit.internal.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestClass extends TestElement {
	private final Class<?> fClass;

	public TestClass(Class<?> klass) {
		fClass= klass;
		if (klass != null && klass.getConstructors().length > 1)
			throw new IllegalArgumentException("Test class can only have one constructor");
	}

	public List<FrameworkMethod> getTestMethods() {
		return getAnnotatedMethods(Test.class);
	}

	@Override
	public List<FrameworkMethod> getBefores() {
		return getAnnotatedMethods(BeforeClass.class);
	}

	@Override
	public List<FrameworkMethod> getAfters() {
		return getAnnotatedMethods(AfterClass.class);
	}

	public List<FrameworkMethod> getAnnotatedMethods(
			Class<? extends Annotation> annotationClass) {
		List<FrameworkMethod> results= new ArrayList<FrameworkMethod>();
		for (Class<?> eachClass : getSuperClasses(fClass)) {
			Method[] methods= eachClass.getDeclaredMethods();
			for (Method eachMethod : methods) {
				Annotation annotation= eachMethod
						.getAnnotation(annotationClass);
				FrameworkMethod testMethod= new FrameworkMethod(eachMethod);
				if (annotation != null && !testMethod.isShadowedBy(results))
					results.add(testMethod);
			}
		}
		if (runsTopToBottom(annotationClass))
			Collections.reverse(results);
		return results;
	}

	private boolean runsTopToBottom(Class<? extends Annotation> annotation) {
		return annotation.equals(Before.class)
				|| annotation.equals(BeforeClass.class);
	}

	private List<Class<?>> getSuperClasses(Class<?> testClass) {
		ArrayList<Class<?>> results= new ArrayList<Class<?>>();
		Class<?> current= testClass;
		while (current != null) {
			results.add(current);
			current= current.getSuperclass();
		}
		return results;
	}

	public Constructor<?> getConstructor() throws SecurityException {
		return fClass.getConstructors()[0];
	}

	public Class<?> getJavaClass() {
		return fClass;
	}

	public String getName() {
		if (fClass == null)
			return "null";
		return fClass.getName();
	}

	public void validateMethods(Class<? extends Annotation> annotation,
			boolean isStatic, List<Throwable> errors) {
		List<FrameworkMethod> methods= getAnnotatedMethods(annotation);

		for (FrameworkMethod eachTestMethod : methods) {
			eachTestMethod.validate(isStatic, errors);
		}
	}

	public void validateStaticMethods(List<Throwable> errors) {
		validateMethods(BeforeClass.class, true, errors);
		validateMethods(AfterClass.class, true, errors);
	}

	public void validateNoArgConstructor(List<Throwable> errors) {
		try {
			getConstructor();
		} catch (Exception e) {
			errors.add(new Exception(
					"Test class should have public zero-argument constructor",
					e));
		}
	}

	public void validateInstanceMethods(List<Throwable> errors) {
		validateMethods(After.class, false, errors);
		validateMethods(Before.class, false, errors);
		validateMethods(Test.class, false, errors);

		List<FrameworkMethod> methods= getAnnotatedMethods(Test.class);
		if (methods.size() == 0)
			errors.add(new Exception("No runnable methods"));
	}

	public void validateMethodsForDefaultRunner(List<Throwable> errors) {
		validateNoArgConstructor(errors);
		validateStaticMethods(errors);
		validateInstanceMethods(errors);
	}

	public Constructor<?> getOnlyConstructor() {
		Constructor<?>[] constructors= fClass.getConstructors();
		Assert.assertEquals(1, constructors.length);
		return constructors[0];
	}

	public Annotation[] getAnnotations() {
		if (fClass == null)
			return new Annotation[0];
		return fClass.getAnnotations();
	}
}
