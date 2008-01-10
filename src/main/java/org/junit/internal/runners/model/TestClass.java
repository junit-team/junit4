package org.junit.internal.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestClass extends TestElement {
	private final Class<?> fClass;

	private Map<Class<?>, List<FrameworkMethod>> methodsForAnnotations= new HashMap<Class<?>, List<FrameworkMethod>>();

	public TestClass(Class<?> klass) {
		fClass= klass;
		if (klass != null && klass.getConstructors().length > 1)
			throw new IllegalArgumentException(
					"Test class can only have one constructor");

		for (Class<?> eachClass : getSuperClasses(fClass))
			for (Method eachMethod : eachClass.getDeclaredMethods())
				addToAnnotationLists(new FrameworkMethod(eachMethod));
	}

	private void addToAnnotationLists(FrameworkMethod testMethod) {
		for (Annotation each : testMethod.getMethod().getAnnotations())
			addToAnnotationList(each.annotationType(), testMethod);
	}

	private void addToAnnotationList(Class<? extends Annotation> annotation,
			FrameworkMethod testMethod) {
		ensureKey(annotation);

		// TODO: (Jan 10, 2008 12:18:09 AM) pass-through
		addToAppropriateEnd(annotation, testMethod);
	}

	private void addToAppropriateEnd(Class<? extends Annotation> annotation, 
			FrameworkMethod testMethod) {
		List<FrameworkMethod> list= methodsForAnnotations.get(annotation);
		if (testMethod.isShadowedBy(list))
			return;
		if (runsTopToBottom(annotation))
			list.add(0, testMethod);
		else
			list.add(testMethod);
	}

	private void ensureKey(Class<? extends Annotation> annotation) {
		if (!methodsForAnnotations.containsKey(annotation))
			methodsForAnnotations.put(annotation,
					new ArrayList<FrameworkMethod>());
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
		ensureKey(annotationClass);
		return methodsForAnnotations.get(annotationClass);
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

		for (FrameworkMethod eachTestMethod : methods)
			eachTestMethod.validate(isStatic, errors);
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
