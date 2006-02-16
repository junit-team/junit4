package org.junit.tests;

import org.junit.internal.runners.InitializationError;
import org.junit.runners.Suite;

public class Enclosed extends Suite {
	public Enclosed(Class<?> klass) throws InitializationError {
		super(klass, klass.getClasses());
	}
}
