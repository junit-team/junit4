/**
 * 
 */
package org.junit.experimental.theories;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ParameterSignature {
	public static ArrayList<ParameterSignature> signatures(Method method) {
		ArrayList<ParameterSignature> sigs= new ArrayList<ParameterSignature>();
		for (int i= 0; i < method.getParameterTypes().length; i++) {
			sigs.add(new ParameterSignature(method.getParameterTypes()[i],
					method.getParameterAnnotations()[i]));
		}
		return sigs;
	}


	public static List<ParameterSignature> signatures(
			Constructor<?> constructor) {
		// TODO: (Oct 12, 2007 12:33:06 PM) handle DUP above
		ArrayList<ParameterSignature> sigs= new ArrayList<ParameterSignature>();
		for (int i= 0; i < constructor.getParameterTypes().length; i++) {
			sigs.add(new ParameterSignature(constructor.getParameterTypes()[i],
					constructor.getParameterAnnotations()[i]));
		}
		return sigs;
	}

	final Class<?> type;

	private final Annotation[] annotations;

	private ParameterSignature(Class<?> type, Annotation[] annotations) {
		this.type= type;
		this.annotations= annotations;
	}

	public boolean canAcceptType(Class<?> candidate) {
		return type.isAssignableFrom(candidate);
	}

	public Class<?> getType() {
		return type;
	}

	public List<Annotation> getAnnotations() {
		return Arrays.asList(annotations);
	}

	public boolean canAcceptArrayType(Class<?> type) {
		return type.isArray() && canAcceptType(type.getComponentType());
	}

	public boolean hasAnnotation(Class<? extends Annotation> type) {
		return getAnnotation(type) != null;
	}

	public <T extends Annotation> T findDeepAnnotation(
			Class<T> annotationType) {
		Annotation[] annotations2= annotations;
		return findDeepAnnotation(annotations2, annotationType, 3);
	}

	private <T extends Annotation> T findDeepAnnotation(Annotation[] annotations,
			Class<T> annotationType, int depth) {
		if (depth == 0)
			return null;
		for (Annotation each : annotations) {
			if (annotationType.isInstance(each))
				return annotationType.cast(each);
			Annotation candidate = findDeepAnnotation(each.annotationType().getAnnotations(), annotationType, depth - 1);
			if (candidate != null)
				return annotationType.cast(candidate);
		}
	
		return null;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		for (Annotation each : getAnnotations())
			if (annotationType.isInstance(each))
				return annotationType.cast(each);
		return null;
	}
}