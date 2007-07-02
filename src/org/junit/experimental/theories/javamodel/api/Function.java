package org.junit.experimental.theories.javamodel.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.experimental.theories.methods.api.ParameterSignature;


public abstract class Function {
	public abstract Method getMethod();

	public abstract Object getTarget();

	public Object invoke(Object... paramArray) throws Throwable {
		getMethod().setAccessible(true);
		Object[] modParams = modifyParameters(paramArray);
		if (modParams.length != parameterTypes().length)
			throw new IllegalArgumentException(String.format(
					"Wrong number of parameters, passing %s to %s", Arrays
							.asList(modParams), getMethod()));
		try {
			return getMethod().invoke(getTarget(), modParams);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	public Throwable exceptionThrown(Object... paramArray) {
		try {
			invoke(paramArray);
		} catch(Throwable t) {
			return t;
		}
		
		return null;
	}

	protected Object[] modifyParameters(Object... paramArray) {
		return paramArray;
	}

	public Type[] parameterTypes() {
		return getMethod().getGenericParameterTypes();
	}

	public Object[] emptyParameterArray() {
		return new Object[parameterTypes().length];
	}

	public Function curryWith(final Object[] curriedParameters) {
		return new Function() {

			@Override public Method getMethod() {
				return Function.this.getMethod();
			}

			@Override public Object getTarget() {
				return Function.this.getTarget();
			}

			@Override protected Object[] modifyParameters(Object... paramArray) {
				ArrayList<Object> list = new ArrayList<Object>();
				list.addAll(Arrays.asList(curriedParameters));
				list.addAll(Arrays.asList(paramArray));
				return list.toArray();
			}
		};
	}

	public ArrayList<ParameterSignature> signatures() {
		return ParameterSignature.signatures(getMethod());
	}
}
