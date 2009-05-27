/**
 * 
 */
package org.junit.experimental.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class Interceptors extends BlockJUnit4ClassRunner {
	public Interceptors(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);
		for (FrameworkField each : interceptorFields())
			validateField(each.getField(), errors);
	}

	private void validateField(Field field, List<Throwable> errors) {
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
		Statement result= statement;
		for (FrameworkField each : interceptorFields())
			try {
				StatementInterceptor interceptor= (StatementInterceptor) each
					.get(target);
				result= interceptor.intercept(result, method);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"How did getFields return a field we couldn't access?");
			}
		return result;
	}

	private List<FrameworkField> interceptorFields() {
		return getTestClass().getAnnotatedFields(Interceptor.class);
	}
}