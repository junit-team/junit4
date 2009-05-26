/**
 * 
 */
package org.junit.experimental.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class Interceptors extends BlockJUnit4ClassRunner {
	public Interceptors(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		// TODO Auto-generated method stub
		super.collectInitializationErrors(errors);
		// TODO (May 18, 2009 10:44:37 PM): This misses superclasses
		// TODO (May 18, 2009 10:44:53 PM): duplication with below
		Field[] fields= getTestClass().getJavaClass().getDeclaredFields();
		for (Field each : fields) {
			// TODO (May 18, 2009 10:30:03 PM): validate, validate!
			validateField(each, errors);
		}
	}

	private void validateField(Field field, List<Throwable> errors) {
		if (field.getAnnotation(Interceptor.class) == null)
			return;
		if (!StatementInterceptor.class.isAssignableFrom(field.getType()))
			errors.add(new Exception("Field " + field.getName()
					+ " must implement StatementInterceptor"));
		if (!Modifier.isPublic(field.getModifiers()))
			errors.add(new Exception("Field " + field.getName()
					+ " must be public"));
	}

	@Override
	protected Statement withBefores(FrameworkMethod method, Object target,
			Statement statement) {
		return super.withBefores(method, target, intercept(statement, target,
				method));
	}

	private Statement intercept(Statement statement, Object target,
			FrameworkMethod method) {
		Class<?> javaClass= getTestClass().getJavaClass();
		Statement result= statement;
		Field[] fields= javaClass.getFields();
		for (Field each : fields) {
			// TODO (May 18, 2009 10:30:03 PM): validate, validate!
			if (each.getAnnotation(Interceptor.class) != null) {
				try {
					StatementInterceptor interceptor= (StatementInterceptor) each
							.get(target);
					result= interceptor.intercept(result, method);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(
							"How did getFields return a field we couldn't access?");
				}
			}
		}
		return result;
	}
}