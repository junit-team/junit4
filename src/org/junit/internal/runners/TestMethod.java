package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.junit.runner.Description;
import org.junit.runners.MethodRunner;
import org.junit.runners.RunMethodWith;

public class TestMethod {
	private final Object fTest;

	private final Method fJavaMethod;

	private final Description fDescription;

	public TestMethod(Object test, Method javaMethod, Description description) {
		fTest= test;
		fJavaMethod= javaMethod;
		fDescription= description;
	}

	public Description getDescription() {
		return fDescription;
	}

	public Object getTest() {
		return fTest;
	}

	public Method getJavaMethod() {
		return fJavaMethod;
	}

	MethodRunner findCustomRunner(TestClassMethodsRunner testClassMethodsRunner)
			throws Exception {
		Method method= getJavaMethod();
		AnnotatedElement method2= method;
		MethodRunner methodRunner= createCustomRunner(method2);
		if (methodRunner != null)
			return methodRunner;

		for (Annotation a : method2.getAnnotations()) {
			AnnotatedElement annotationType= a.annotationType();
			MethodRunner arunner= createCustomRunner(annotationType);
			if (arunner != null)
				return arunner;
		}

		return null;
	}

	private MethodRunner createCustomRunner(AnnotatedElement annotationType)
			throws Exception {
		RunMethodWith runMethodWith= annotationType
				.getAnnotation(RunMethodWith.class);
		if (runMethodWith != null)
			return runMethodWith.value().newInstance();
		return null;
	}

}
