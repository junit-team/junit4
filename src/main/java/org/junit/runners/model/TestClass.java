package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Wraps a class to be run, providing method validation and annotation searching
 */
public class TestClass {
	private final Class<?> fClass;

	private Map<Class<?>, List<FrameworkMethod>> fMethodsForAnnotations= new HashMap<Class<?>, List<FrameworkMethod>>();

	/**
	 * Creates a {@code TestClass} wrapping {@code klass}. Each time this
	 * constructor executes, the class is scanned for annotations, which can be
	 * an expensive process (we hope in future JDK's it will not be.) Therefore,
	 * try to share instances of {@code TestClass} where possible.
	 */
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
		for (Annotation each : computeAnnotations(testMethod))
			addToAnnotationList(each.annotationType(), testMethod);
	}

	/**
	 * Returns all of the annotations on {@code testMethod}
	 */
	protected Annotation[] computeAnnotations(FrameworkMethod testMethod) {
		return testMethod.getAnnotations();
	}

	private void addToAnnotationList(Class<? extends Annotation> annotation,
			FrameworkMethod testMethod) {
		List<FrameworkMethod> methods= getAnnotatedMethods(annotation);
		if (testMethod.isShadowedBy(methods))
			return;
		if (runsTopToBottom(annotation))
			methods.add(0, testMethod);
		else
			methods.add(testMethod);
	}

	private void ensureKey(Class<? extends Annotation> annotation) {
		if (!fMethodsForAnnotations.containsKey(annotation))
			fMethodsForAnnotations.put(annotation,
					new ArrayList<FrameworkMethod>());
	}

	/**
	 * Returns, efficiently, all the non-overridden methods in this class and
	 * its superclasses that are annotated with {@code annotationClass}.
	 */
	public List<FrameworkMethod> getAnnotatedMethods(
			Class<? extends Annotation> annotationClass) {
		ensureKey(annotationClass);
		return fMethodsForAnnotations.get(annotationClass);
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

	/**
	 * Returns the underlying Java class.
	 */
	public Class<?> getJavaClass() {
		return fClass;
	}

	/**
	 * Returns the class's name.
	 */
	public String getName() {
		if (fClass == null)
			return "null";
		return fClass.getName();
	}

	/**
	 * Returns the only public constructor in the class, or throws an {@code
	 * AssertionError} if there are more or less than one.
	 */

	public Constructor<?> getOnlyConstructor() {
		Constructor<?>[] constructors= fClass.getConstructors();
		Assert.assertEquals(1, constructors.length);
		return constructors[0];
	}

	/**
	 * Returns the annotations on this class
	 */
	public Annotation[] getAnnotations() {
		if (fClass == null)
			return new Annotation[0];
		return fClass.getAnnotations();
	}
}
