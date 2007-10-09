package org.junit.runners;

import org.junit.internal.runners.model.InitializationError;

public class Enclosed extends Suite {
	public Enclosed(Class<?> klass) throws InitializationError {
		super(klass, klass.getClasses());
	}
}
