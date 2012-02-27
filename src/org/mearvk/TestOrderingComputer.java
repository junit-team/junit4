package org.mearvk;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * DO NOT USE - MAY BE DROPPED AS UNNECESSARY
 * 
 * An ordered test computer. Used to explicity bypass the default JUnit runner
 * build process; a bit faster and cleaner.
 * 
 * @see "http://code.google.com/p/junit-test-orderer/"
 * 
 * @author Max Rupplin
 */
public class TestOrderingComputer extends Computer
{
	@Override
	public Runner getSuite(final RunnerBuilder builder, Class<?>[] classes)
			throws InitializationError
	{
		// return new OrderedSuite(classes); //end of Suite constructor

		return null;
	}
}

class MyRunnerBuilder extends RunnerBuilder
{
	RunnerBuilder builder = null;

	public MyRunnerBuilder(RunnerBuilder builder)
	{
		this.builder = builder;
	}

	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable
	{
		return getRunner(builder, testClass);
	}

	/**
	 * Create a single-class runner for {@code testClass}, using {@code builder}
	 */
	protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
			throws Throwable
	{
		return builder.runnerForClass(testClass);
	}
}
