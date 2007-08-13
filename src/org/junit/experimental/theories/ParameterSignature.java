/**
 * 
 */
package org.junit.experimental.theories;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

	public ParameterSupplier getAnnotatedSupplier()
			throws InstantiationException, IllegalAccessException {
		Annotation annotation= getSupplierAnnotation();
		if (annotation != null)
			return getSupplier(annotation).value().newInstance();
		return null;
	}

	public ParameterSupplier getSupplier(Class<?> targetClass)
			throws InstantiationException, IllegalAccessException {
		ParameterSupplier supplier= getAnnotatedSupplier();
		if (supplier != null)
			return supplier;
	
		return ParameterSignature.fieldParameterSupplier(targetClass);
	}

	public List<PotentialParameterValue> getPotentialValues(Object container)
			throws InstantiationException, IllegalAccessException {
		return getSupplier(container.getClass()).getValueSources(container, this);
	}

	public static ParameterSupplier fieldParameterSupplier(final Class<?> targetClass) {
		return new ParameterSupplier() {
			@Override
			public List<PotentialParameterValue> getValueSources(final Object test, ParameterSignature sig) {
				List<PotentialParameterValue> list= new ArrayList<PotentialParameterValue>();
				for (final Field field : targetClass.getFields()) {
					if (sig.canAcceptField(field)) {
						try {
						list.add(PotentialParameterValue.forValue(field.get(test)));
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(
									"unexpected: field from getClass doesn't exist on object");
						} catch (IllegalAccessException e) {
							throw new RuntimeException(
									"unexpected: getFields returned an inaccessible field");
						}
					}
				}
				for (final Method method : targetClass.getMethods()) {
					if (method.getParameterTypes().length == 0
							&& sig.canAcceptMethod(method)) {
							list.add(new PotentialParameterValue() {
								@Override
								public Object getValue() throws CouldNotGenerateValueException {
								try {
									return method.invoke(test);
								} catch (IllegalArgumentException e) {
									throw new RuntimeException(
											"unexpected: argument length is checked");
								} catch (IllegalAccessException e) {
									throw new RuntimeException(
											"unexpected: getMethods returned an inaccessible method");
								} catch (InvocationTargetException e) {
									throw new CouldNotGenerateValueException();
									// do nothing, just look for more values
								}
							}
						});
					}
				}
				return list;
			}
		};
	}

	public Class<?> getType() {
		return type;
	}
}