package org.junit.internal.runners.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;

// TODO: (Oct 8, 2007 1:58:24 PM) extract InvokedMethod (which can be Befores, Afters, Tests, etc.)
//   This should be just @Test methods (and @Theory)

public class TestMethod extends TestElement {
	private final TestClass fTestClass;
	private final Method fMethod;

	public TestMethod(Method method, TestClass testClass) {
		fMethod = method;
		fTestClass= testClass;
	}

	public Class<? extends Throwable> getExpectedException() {
		Test annotation= fMethod.getAnnotation(Test.class);
		if (annotation == null || annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

	public boolean expectsException() {
		return getExpectedException() != null;
	}

	public long getTimeout() {
		Test annotation= fMethod.getAnnotation(Test.class);
		if (annotation == null)
			return 0;
		long timeout= annotation.timeout();
		return timeout;
	}

	public boolean isIgnored() {
		return fMethod.getAnnotation(Ignore.class) != null;
	}
	
	@Override
	public List<TestMethod> getBefores() {
		return fTestClass.getAnnotatedMethods(Before.class);
	}

	@Override
	public List<TestMethod> getAfters() {
		return fTestClass.getAnnotatedMethods(After.class);
	}

	public Method getMethod() {	
		return fMethod;
	}

	public Object invokeExplosively(final Object target, final Object... params) throws Throwable {
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

	public Class<?>[] getParameterTypes() {
		return fMethod.getParameterTypes();
	}

	public void validate(boolean isStatic, List<Throwable> errors) {
		if (Modifier.isStatic(fMethod.getModifiers()) != isStatic) {
			String state= isStatic ? "should" : "should not";
			errors.add(new Exception("Method " + fMethod.getName() + "() "
					+ state + " be static"));
		}
		if (!Modifier.isPublic(fMethod.getDeclaringClass().getModifiers()))
			errors.add(new Exception("Class " + fMethod.getDeclaringClass().getName()
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

	public boolean isShadowedBy(TestMethod each) {
		if (! each.getName().equals(getName()))
			return false;
		if (each.getParameterTypes().length != getParameterTypes().length)
			return false;
		for (int i= 0; i < each.getParameterTypes().length; i++) {
			if (! each.getParameterTypes()[i].equals(getParameterTypes()[i]))
				return false;
		}
		return true;
	}

	boolean isShadowedBy(List<TestMethod> results) {
		for (TestMethod each : results) {
			if (isShadowedBy(each))
				return true;
		}
		return false;
	}
}