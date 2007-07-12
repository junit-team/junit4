/**
 * 
 */
package org.junit.experimental.theories.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.methods.api.ParameterSignature;
import org.junit.experimental.theories.methods.api.ParameterSupplier;

public class TheoryContainerReference {
	private final Object container;

	public TheoryContainerReference(Object target) {
		this.container= target;
	}

	public int invoke(TheoryMethod method, Object[] params) throws Throwable {
		try {
			getTheoryFunction(method).invoke(params);
		} catch (AssumptionViolatedException e) {
			method.addAssumptionFailure(e);
			return 0;
		} catch (Throwable e) {
			if (params.length == 0)
				throw e;
			throw new ParameterizedAssertionError(e, method.getMethod()
					.getName(), params);
		}
		return 1;
	}

	private ConcreteFunction getTheoryFunction(TheoryMethod method) {
		return new ConcreteFunction(container, method.getMethod());
	}

	public List<?> getPotentialValues(ParameterSignature sig)
			throws InstantiationException, IllegalAccessException {
		return getSupplier(sig).getValues(container, sig);
	}

	private ParameterSupplier getSupplier(ParameterSignature sig)
			throws InstantiationException, IllegalAccessException {
		ParameterSupplier supplier= getAnnotatedSupplier(sig);
		if (supplier != null)
			return supplier;

		return fieldParameterSupplier();
	}

	private ParameterSupplier getAnnotatedSupplier(ParameterSignature sig)
			throws InstantiationException, IllegalAccessException {
		Annotation annotation= sig.getSupplierAnnotation();
		if (annotation != null)
			return sig.getSupplier(annotation).value().newInstance();
		return null;
	}

	private ParameterSupplier fieldParameterSupplier() {
		return new ParameterSupplier() {
			@Override
			public List<Object> getValues(Object test, ParameterSignature sig) {
				ArrayList<Object> list= new ArrayList<Object>();
				for (Field field : container.getClass().getFields()) {
					if (sig.canAcceptField(field)) {
						try {
							list.add(field.get(test));
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(
									"unexpected: field from getClass doesn't exist on object");
						} catch (IllegalAccessException e) {
							throw new RuntimeException(
									"unexpected: getFields returned an inaccessible field");
						}
					}
				}
				for (Method method : container.getClass().getMethods()) {
					if (method.getParameterTypes().length == 0
							&& sig.canAcceptMethod(method)) {
						try {
							list.add(method.invoke(test));
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(
									"unexpected: argument length is checked");
						} catch (IllegalAccessException e) {
							throw new RuntimeException(
									"unexpected: getMethods returned an inaccessible method");
						} catch (InvocationTargetException e) {
							// do nothing, just look for more values
						}
					}
				}
				return list;
			}
		};
	}

	public int runWithParameters(TheoryMethod method, List<Object> values,
			List<ParameterSignature> sigs) throws Throwable {
		if (sigs.size() == 0) {
			return invoke(method, values.toArray());
		}

		int count= 0;

		for (Object value : getPotentialValues(sigs.get(0))) {
			if (value != null || method.nullsOk())
				count+= runWithParameters(method, concat(values, value), sigs
						.subList(1, sigs.size()));
		}

		return count;
	}

	private List<Object> concat(List<Object> values, Object value) {
		ArrayList<Object> list= new ArrayList<Object>();
		list.addAll(values);
		list.add(value);
		return list;
	}
}