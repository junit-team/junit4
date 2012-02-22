/**
 * 
 */
package org.junit.experimental.theories.internal;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

/**
 * Supplies Theory parameters based on all public members of the target class.
 */
public class AllMembersSupplier extends ParameterSupplier {
	static class MethodParameterValue extends PotentialAssignment {
		private final FrameworkMethod fMethod;

		private MethodParameterValue(FrameworkMethod dataPointMethod) {
			fMethod= dataPointMethod;
		}

		@Override
		public Object getValue() throws CouldNotGenerateValueException {
			try {
				return fMethod.invokeExplosively(null);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(
						"unexpected: argument length is checked");
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"unexpected: getMethods returned an inaccessible method");
			} catch (Throwable e) {
				throw new CouldNotGenerateValueException();
				// do nothing, just look for more values
			}
		}

		@Override
		public String getDescription() throws CouldNotGenerateValueException {
			return fMethod.getName();
		}
	}

	private final TestClass fClass;

	/**
	 * Constructs a new supplier for {@code type}
	 */
	public AllMembersSupplier(TestClass type) {
		fClass= type;
	}

	@Override
	public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
		List<PotentialAssignment> list= new ArrayList<PotentialAssignment>();

		addFields(sig, list);
		addSinglePointMethods(sig, list);
		addMultiPointMethods(list);

		return list;
	}

	private void addMultiPointMethods(List<PotentialAssignment> list) {
		for (FrameworkMethod dataPointsMethod : fClass
				.getAnnotatedMethods(DataPoints.class))
			try {
				addArrayValues(dataPointsMethod.getName(), list, dataPointsMethod.invokeExplosively(null));
			} catch (Throwable e) {
				// ignore and move on
			}
	}

	@SuppressWarnings("deprecation")
	private void addSinglePointMethods(ParameterSignature sig,
			List<PotentialAssignment> list) {
		for (FrameworkMethod dataPointMethod : fClass
				.getAnnotatedMethods(DataPoint.class)) {
			Class<?> type= sig.getType();
			if ((dataPointMethod.producesType(type)))
				list.add(new MethodParameterValue(dataPointMethod));
		}
	}

	private void addFields(ParameterSignature sig,
			List<PotentialAssignment> list) {
		for (final Field field : fClass.getJavaClass().getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				Class<?> type= field.getType();
				if (sig.canAcceptArrayType(type)
						&& field.getAnnotation(DataPoints.class) != null) {
					addArrayValues(field.getName(), list, getStaticFieldValue(field));
				} else if (sig.canAcceptType(type)
						&& field.getAnnotation(DataPoint.class) != null) {
					list.add(PotentialAssignment
							.forValue(field.getName(), getStaticFieldValue(field)));
				}
			}
		}
	}

	private void addArrayValues(String name, List<PotentialAssignment> list, Object array) {
		for (int i= 0; i < Array.getLength(array); i++)
			list.add(PotentialAssignment.forValue(name + "[" + i + "]", Array.get(array, i)));
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