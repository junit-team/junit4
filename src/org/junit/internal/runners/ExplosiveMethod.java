/**
 * 
 */
package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * When invoked, throws the exception from the reflected method, rather than
 * wrapping it in an InvocationTargetException.
 */
public class ExplosiveMethod {
	public static ExplosiveMethod from(Method method) {
		return new ExplosiveMethod(method);
	}

	private Method fMethod;

	private ExplosiveMethod(Method method) {
		fMethod= method;
	}

	public Method getMethod() {
		return fMethod;
	}

	public void invoke(Object target, Object... params) throws IllegalAccessException, Throwable {
		try {
			getMethod().invoke(target, params);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}