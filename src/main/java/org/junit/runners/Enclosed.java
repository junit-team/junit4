package org.junit.runners;

import org.junit.internal.runners.InitializationError;

public class Enclosed extends Suite {
	public Enclosed(Class<?> klass, SuiteBuilder builder) throws InitializationError {
		super(builder, klass, klass.getClasses());
	}
}
