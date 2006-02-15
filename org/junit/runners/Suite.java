package org.junit.runners;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Request;

public class Suite extends TestClassRunner {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteClasses {
		public Class[] value();
	}

	public Suite(Class<?> klass) throws InitializationError {
		this(klass, getAnnotatedClasses(klass));
	}

	public Suite(Class<?> klass, Class[] annotatedClasses) throws InitializationError {
		super(klass, Request.classes(klass.getName(), annotatedClasses).getRunner());
	}

	private static Class[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
		SuiteClasses annotation= klass.getAnnotation(SuiteClasses.class);
		if (annotation == null)
			throw new InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName()));
		return annotation.value();
	}
}
