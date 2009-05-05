/**
 * 
 */
package org.junit.experimental.interceptor;

import java.lang.reflect.Field;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class Interceptors extends BlockJUnit4ClassRunner {
	public Interceptors(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected Statement withBefores(FrameworkMethod method, Object target,
			Statement statement) {
		// TODO (Apr 28, 2009 10:55:21 PM): parameter clump?
		return super.withBefores(method, target, intercept(statement, target, method));
	}

	private Statement intercept(Statement statement, Object target, FrameworkMethod method) {
		Class<?> javaClass= getTestClass().getJavaClass();
		Field[] fields= javaClass.getFields();
		Statement result= statement;
		for (Field each : fields) {
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