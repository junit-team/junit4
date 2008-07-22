package org.junit.runners;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Ignore;
import org.junit.internal.runners.model.ReflectiveCallable;

public class FrameworkMethod {
	private final Method fMethod;

	public FrameworkMethod(Method method) {
		fMethod= method;
	}

	public boolean isIgnored() {
		return fMethod.getAnnotation(Ignore.class) != null;
	}

	public Method getMethod() {
		return fMethod;
	}

	public Object invokeExplosively(final Object target, final Object... params)
			throws Throwable {
		return new ReflectiveCallable() {
			@Override
			protected Object runReflectiveCall() throws Throwable {
				return fMethod.invoke(target, params);
			}
		}.run();
	}

	public String getName() {
		return fMethod.getName();
	}

	private Class<?>[] getParameterTypes() {
		return fMethod.getParameterTypes();
	}

	public void validate(boolean isStatic, List<Throwable> errors) {
		if (Modifier.isStatic(fMethod.getModifiers()) != isStatic) {
			String state= isStatic ? "should" : "should not";
			errors.add(new Exception("Method " + fMethod.getName() + "() "
					+ state + " be static"));
		}
		if (!Modifier.isPublic(fMethod.getDeclaringClass().getModifiers()))
			errors.add(new Exception("Class "
					+ fMethod.getDeclaringClass().getName()
					+ " should be public"));
		if (!Modifier.isPublic(fMethod.getModifiers()))
			errors.add(new Exception("Method " + fMethod.getName()
					+ " should be public"));
		if (fMethod.getReturnType() != Void.TYPE)
			errors.add(new Exception("Method " + fMethod.getName()
					+ " should be void"));
		if (fMethod.getParameterTypes().length != 0)
			errors.add(new Exception("Method " + fMethod.getName()
					+ " should have no parameters"));
	}

	public boolean isShadowedBy(FrameworkMethod each) {
		if (!each.getName().equals(getName()))
			return false;
		if (each.getParameterTypes().length != getParameterTypes().length)
			return false;
		for (int i= 0; i < each.getParameterTypes().length; i++) {
			if (!each.getParameterTypes()[i].equals(getParameterTypes()[i]))
				return false;
		}
		return true;
	}

	public boolean isShadowedBy(List<FrameworkMethod> results) {
		for (FrameworkMethod each : results)
			if (isShadowedBy(each))
				return true;
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! FrameworkMethod.class.isInstance(obj))
			return false;
		return ((FrameworkMethod)obj).fMethod.equals(fMethod);
	}
	
	@Override
	public int hashCode() {
		return fMethod.hashCode();
	}

	public boolean producesType(Class<?> type) {
		return getParameterTypes().length == 0
				&& type.isAssignableFrom(fMethod.getReturnType());
	}
}