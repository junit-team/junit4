/**
 * 
 */
package org.junit.experimental.theories.internal;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;

public class AssignmentRequest {
	private static class MethodParameterValue extends PotentialAssignment {
		private final Method fMethod;

		private final Object fTest;

		private MethodParameterValue(Method method, Object test) {
			fMethod= method;
			fTest= test;
		}

		@Override
		public Object getValue() throws CouldNotGenerateValueException {
			try {
				return fMethod.invoke(fTest);
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
	}
	
	private Object target;

	private ParameterSignature sig;

	public AssignmentRequest(Object target, ParameterSignature sig) {
		this.target= target;
		this.sig= sig;
	}

	public List<PotentialAssignment> getPotentialAssignments()
			throws InstantiationException, IllegalAccessException {
		return getSupplier().getValueSources(target, sig);
	}

	public ParameterSupplier getSupplier() throws InstantiationException,
			IllegalAccessException {
		ParameterSupplier supplier= getAnnotatedSupplier();
		if (supplier != null)
			return supplier;

		return fieldParameterSupplier();
	}

	public ParameterSupplier getAnnotatedSupplier()
			throws InstantiationException, IllegalAccessException {
		ParametersSuppliedBy annotation= sig.findDeepAnnotation(ParametersSuppliedBy.class);
		if (annotation == null)
			return null;
		return annotation.value().newInstance();
	}
	
	public ParameterSupplier fieldParameterSupplier() {
		final Class<? extends Object> targetClass= target.getClass();
		return new ParameterSupplier() {
			@Override
			public List<PotentialAssignment> getValueSources(
					final Object test, ParameterSignature sig) {
				List<PotentialAssignment> list= new ArrayList<PotentialAssignment>();
				for (final Field field : targetClass.getFields()) {
					Class<?> type= field.getType();
					if (sig.canAcceptType(type)) {
						list.add(PotentialAssignment
								.forValue(getFieldValue(field, test)));
					} else if (sig.canAcceptArrayType(type)) {
						addArrayValues(list, getFieldValue(field, test));
					}
				}
				for (final Method method : targetClass.getMethods()) {
					if ((method.getParameterTypes().length == 0 && sig.getType()
							.isAssignableFrom(method.getReturnType()))
							&& method.isAnnotationPresent(DataPoint.class)) {
						list.add(new MethodParameterValue(method, test));
					} else if (method.isAnnotationPresent(DataPoints.class)) {
						try {
							addArrayValues(list, method.invoke(test));
						} catch (Exception e) {
							// ignore and move on
						}
					}
				}
				return list;
			}

			private void addArrayValues(List<PotentialAssignment> list,
					Object array) {
				for (int i= 0; i < Array.getLength(array); i++)
					list.add(PotentialAssignment.forValue(Array.get(array,
							i)));
			}

			private Object getFieldValue(final Field field, final Object object) {
				try {
					return field.get(object);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(
							"unexpected: field from getClass doesn't exist on object");
				} catch (IllegalAccessException e) {
					throw new RuntimeException(
							"unexpected: getFields returned an inaccessible field");
				}
			}
		};
	}
}