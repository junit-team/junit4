/**
 * 
 */
package org.junit.experimental.theories.internal;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

class AllMembersSupplier extends ParameterSupplier {
	static class MethodParameterValue extends PotentialAssignment {
		private final Method fMethod;

		private MethodParameterValue(Method method) {
			fMethod= method;
		}

		@Override
		public Object getValue() throws CouldNotGenerateValueException {
			try {
				return fMethod.invoke(null);
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
	
	private final Class<?> fClass;
	
	public AllMembersSupplier(Class<?> type) {
		fClass = type;
	}
	
	@Override
	public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
		List<PotentialAssignment> list= new ArrayList<PotentialAssignment>();
		for (final Field field : fClass.getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				Class<?> type= field.getType();
				if (sig.canAcceptType(type)) {
					list.add(PotentialAssignment
							.forValue(getStaticFieldValue(field)));
				} else if (sig.canAcceptArrayType(type)) {
					addArrayValues(list, getStaticFieldValue(field));
				}
			}
		}

		for (final Method method : fClass.getMethods()) {
			if ((method.getParameterTypes().length == 0 && sig.getType()
					.isAssignableFrom(method.getReturnType()))
					&& method.isAnnotationPresent(DataPoint.class)) {
				list.add(new MethodParameterValue(method));
			} else if (method.isAnnotationPresent(DataPoints.class)) {
				try {
					addArrayValues(list, method.invoke(null));
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

	private Object getStaticFieldValue(final Field field) {
		try {
			return field.get(null);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
					"unexpected: field from getClass doesn't exist on object");
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"unexpected: getFields returned an inaccessible field");
		}
	}
}