package org.junit.runner;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class Executioner {

	public Suite getSuite(final RunnerBuilder builder,
			Class<?>[] classes) throws InitializationError {
		return new Suite(new RunnerBuilder() {
			@Override
			public Runner runnerForClass(Class<?> testClass) throws Throwable {
				return getRunner(builder, testClass);
			}
		}, classes);
	}

	protected Runner getRunner(RunnerBuilder builder, Class<?> testClass) throws Throwable {
		return builder.runnerForClass(testClass);
	}
}
