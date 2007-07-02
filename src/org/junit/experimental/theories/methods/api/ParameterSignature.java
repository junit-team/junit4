/**
 * 
 */
package org.junit.experimental.theories.methods.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ParameterSignature {
	public static ArrayList<ParameterSignature> signatures(Method method) {
		ArrayList<ParameterSignature> sigs = new ArrayList<ParameterSignature>();
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			sigs.add(new ParameterSignature(method.getParameterTypes()[i],
					method.getParameterAnnotations()[i]));
		}
		return sigs;
	}

	private final Class<?> type;
	private final Annotation[] annotations;

	private ParameterSignature(Class<?> type, Annotation[] annotations) {
		this.type = type;
		this.annotations = annotations;
	}

	public Annotation getSupplierAnnotation() {
		for (Annotation annotation : annotations) {
			if (getSupplier(annotation) != null)
				return annotation;
		}

		return null;
	}

	public ParametersSuppliedBy getSupplier(Annotation annotation) {
		return annotation.annotationType().getAnnotation(
				ParametersSuppliedBy.class);
	}

	public boolean canAcceptField(Field field) {
		return type.isAssignableFrom(field.getType());
	}

	public boolean canAcceptMethod(Method method) {
		return method.isAnnotationPresent(DataPoint.class)
				&& type.isAssignableFrom(method.getReturnType());
	}
}