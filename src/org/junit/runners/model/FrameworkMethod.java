package org.junit.runners.model; 

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.internal.runners.model.ReflectiveCallable;

/**
 * Represents a method on a test class to be invoked at the appropriate point in
 * test execution. These methods are usually marked with an annotation (such as
 * {@code @Test}, {@code @Before}, {@code @After}, {@code @BeforeClass}, 
 * {@code @AfterClass}, etc.)
 */
public class FrameworkMethod extends FrameworkMember<FrameworkMethod> {
	final Method fMethod;

	/**
	 * Returns a new {@code FrameworkMethod} for {@code method}
	 */
	public FrameworkMethod(Method method) {
		fMethod= method;
	}

	/**
	 * Returns the underlying Java method
	 */
	public Method getMethod() {
		return fMethod;
	}

	/**
	 * Returns the result of invoking this method on {@code target} with
	 * parameters {@code params}. {@link InvocationTargetException}s thrown are
	 * unwrapped, and their causes rethrown.
	 */
	public Object invokeExplosively(final Object target, final Object... params)
			throws Throwable {
		return new ReflectiveCallable() {
			@Override
			protected Object runReflectiveCall() throws Throwable {
				return fMethod.invoke(target, params);
			}
		}.run();
	}

	/**
	 * Returns the method's name
	 */
	public String getName() {
		return fMethod.getName();
	}

	/**
	 * Adds to {@code errors} if this method:
	 * <ul>
	 * <li>is not public, or
	 * <li>takes parameters, or
	 * <li>returns something other than void, or
	 * <li>is static (given {@code isStatic is false}), or
	 * <li>is not static (given {@code isStatic is true}).
	 */
	public void validatePublicVoidNoArg(boolean isStatic, List<Throwable> errors) {
		validatePublicVoid(isStatic, errors);
		if (fMethod.getParameterTypes().length != 0)
			errors.add(new Exception("Method " + fMethod.getName() + " should have no parameters"));
	}


	/**
	 * Adds to {@code errors} if this method:
	 * <ul>
	 * <li>is not public, or
	 * <li>returns something other than void, or
	 * <li>is static (given {@code isStatic is false}), or
	 * <li>is not static (given {@code isStatic is true}).
	 */
	public void validatePublicVoid(boolean isStatic, List<Throwable> errors) {
		if (Modifier.isStatic(fMethod.getModifiers()) != isStatic) {
			String state= isStatic ? "should" : "should not";
			errors.add(new Exception("Method " + fMethod.getName() + "() " + state + " be static"));
		}
		if (!Modifier.isPublic(fMethod.getDeclaringClass().getModifiers()))
			errors.add(new Exception("Class " + fMethod.getDeclaringClass().getName() + " should be public"));
		if (!Modifier.isPublic(fMethod.getModifiers()))
			errors.add(new Exception("Method " + fMethod.getName() + "() should be public"));
		if (fMethod.getReturnType() != Void.TYPE)
			errors.add(new Exception("Method " + fMethod.getName() + "() should be void"));
	}

	public void validateNoTypeParametersOnArgs(List<Throwable> errors) {
		new NoGenericTypeParametersValidator(fMethod).validate(errors);
	}

	@Override
	public boolean isShadowedBy(FrameworkMethod other) {
		if (!other.getName().equals(getName()))
			return false;
		if (other.getParameterTypes().length != getParameterTypes().length)
			return false;
		for (int i= 0; i < other.getParameterTypes().length; i++)
			if (!other.getParameterTypes()[i].equals(getParameterTypes()[i]))
				return false;
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!FrameworkMethod.class.isInstance(obj))
			return false;
		return ((FrameworkMethod) obj).fMethod.equals(fMethod);
	}

	@Override
	public int hashCode() {
		return fMethod.hashCode();
	}

	/**
	 * Returns true iff this is a no-arg method that returns a value assignable
	 * to {@code type}
	 *
	 * @deprecated This is used only by the Theories runner, and does not
	 * use all the generic type info that it ought to. It will be replaced
	 * with a forthcoming ParameterSignature#canAcceptResultOf(FrameworkMethod)
	 * once Theories moves to junit-contrib.
	 */
	@Deprecated
	public boolean producesType(Type type) {
		return getParameterTypes().length == 0 && type instanceof Class<?>
		    && ((Class<?>) type).isAssignableFrom(fMethod.getReturnType());
	}

	private Class<?>[] getParameterTypes() {
		return fMethod.getParameterTypes();
	}

	/**
	 * Returns the annotations on this method
	 */
	@Override
	public Annotation[] getAnnotations() {
		return fMethod.getAnnotations();
	}

	/**
	 * Returns the annotation of type {@code annotationType} on this method, if
	 * one exists.
	 */
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		return fMethod.getAnnotation(annotationType);
	}
}
