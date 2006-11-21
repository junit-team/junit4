package org.junit.runners;

import org.junit.internal.runners.InitializationError;

public class Enclosed extends Suite {
	public Enclosed(Class<?> klass) throws InitializationError {
		super(klass, klass.getClasses());
	}
}
