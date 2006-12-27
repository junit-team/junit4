package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;

public class TestIntrospector {
	private static class MethodCollector {
		private List<Method> methods= new ArrayList<Method>();

		private Class<? extends Annotation> annotationClass;

		public MethodCollector(Class<? extends Annotation> annotationClass) {
			this.annotationClass= annotationClass;
		}

		public MethodCollector addMethods(Class<?> testClass) {
			for (Method eachMethod : testClass.getDeclaredMethods()) {
				Annotation annotation= eachMethod
						.getAnnotation(annotationClass);
				if (annotation != null && !isShadowed(eachMethod))
					methods.add(eachMethod);
			}
			
			addSuperclasses(testClass);			
			addMixins(testClass);
			return this;
		}

		private void addMixins(Class<?> testClass) {
			MixIn mixins= testClass.getAnnotation(MixIn.class);
			if (mixins != null)
				for (Class<?> type : mixins.value())					
					addMethods(type);
		}

		private void addSuperclasses(Class<?> testClass) {
			Class<?> superclass= testClass.getSuperclass();
			if (superclass != null)
				addMethods(testClass.getSuperclass());
		}

		private boolean isShadowed(Method method) {
			for (Method each : methods) {
				if (isShadowed(method, each))
					return true;
			}
			return false;
		}

		private boolean isShadowed(Method current, Method previous) {
			if (!previous.getName().equals(current.getName()))
				return false;
			if (previous.getParameterTypes().length != current
					.getParameterTypes().length)
				return false;
			for (int i= 0; i < previous.getParameterTypes().length; i++) {
				if (!previous.getParameterTypes()[i].equals(current
						.getParameterTypes()[i]))
					return false;
			}
			return true;
		}

		public List<Method> getMethods() {
			return methods;
		}
	}

	private final Class<?> fTestClass;

	public TestIntrospector(Class<?> testClass) {
		fTestClass= testClass;
	}

	public List<Method> getTestMethods(
			Class<? extends Annotation> annotationClass) {
		List<Method> results= new MethodCollector(annotationClass).addMethods(
				fTestClass).getMethods();
		if (runsTopToBottom(annotationClass))
			Collections.reverse(results);
		return results;
	}

	public boolean isIgnored(Method eachMethod) {
		return eachMethod.getAnnotation(Ignore.class) != null;
	}

	private boolean runsTopToBottom(Class<? extends Annotation> annotation) {
		return annotation.equals(Before.class)
				|| annotation.equals(BeforeClass.class);
	}

	long getTimeout(Method method) {
		Test annotation= method.getAnnotation(Test.class);
		long timeout= annotation.timeout();
		return timeout;
	}

	Class<? extends Throwable> expectedException(Method method) {
		Test annotation= method.getAnnotation(Test.class);
		if (annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

}
