package org.junit.runners;


public class Enclosed extends Suite {
	public Enclosed(Class<?> klass, RunnerBuilder builder) throws Throwable {
		super(builder, klass, klass.getClasses());
	}
}
