package org.junit.runner;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * Represents a strategy for computing runners and suites.
 * WARNING: this class is very likely to undergo serious changes in version 4.8 and
 * beyond.
 */
public class Computer {
	/**
	 * Returns a new default computer, which runs tests in serial order
	 */
	public static Computer serial() {
		return new Computer();
	}

	/**
	 * Create a suite for {@code classes}, building Runners with {@code builder}.
	 * Throws an InitializationError if Runner construction fails
	 */
	public Runner getSuite(final RunnerBuilder builder,
			Class<?>[] classes) throws InitializationError {
		return new Suite(new RunnerBuilder() {
			@Override
			public Runner runnerForClass(Class<?> testClass) throws Throwable {
				return getRunner(builder, testClass);
			}
		}, classes);
	}

	/**
	 * Create a single-class runner for {@code testClass}, using {@code builder}
	 */
	protected Runner getRunner(RunnerBuilder builder, Class<?> testClass) throws Throwable {
		return builder.runnerForClass(testClass);
	}
}
