package org.junit.experimental.imposterization;

import java.lang.reflect.Method;

import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.junit.experimental.theories.runner.Function;

public class FunctionPointer extends Function {
	public static FunctionPointer pointer() {
		return new FunctionPointer();
	}

	private Method method;
	private Object target;
	
	@SuppressWarnings("unchecked") public <T> T calls(T target) {
		this.target = target;
		Invokable invokable = new Invokable() {
			public Object invoke(Invocation invocation)
					throws Throwable {
				Method invokedMethod = invocation.getInvokedMethod();
				if (!invokedMethod.getName().equals("finalize"))
					method = invokedMethod;
				return null;
			}
		};
		return (T) new PopperImposterizer(invokable).imposterize(target.getClass());
	}
	
	@Override public Method getMethod() {
		return method;
	}

	@Override public Object getTarget() {
		return target;
	}
}
