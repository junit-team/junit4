/**
 * 
 */
package org.junit.experimental.theories.javamodel.api;

import java.lang.reflect.Method;

public class ConcreteFunction extends Function {
	protected final Object target;
	protected final Method method;

	public ConcreteFunction(Object target, Method method) {
		this.target = target;
		this.method = method;
	}

	@Override public Object getTarget() {
		return target;
	}

	@Override public Method getMethod() {
		return method;
	}

	@Override public boolean equals(Object obj) {
		Function function = (Function) obj;

		return targetIs(function.getTarget())
				&& method.equals(function.getMethod());
	}

	private boolean targetIs(Object otherTarget) {
		if (target == null)
			return otherTarget == null;
		return target.equals(otherTarget);
	}

	@Override public String toString() {
		return String.format("%s.%s", target, method);
	}
}