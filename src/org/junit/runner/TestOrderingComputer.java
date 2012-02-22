package org.junit.runner;

import org.junit.runners.OrderedSuite;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class TestOrderingComputer extends Computer
{
	public Runner getSuite(final RunnerBuilder builder, Class<?>[] classes) throws InitializationError 
	{
		return new OrderedSuite(builder, classes); //end of Suite constructor
	}
}

class MyRunnerBuilder extends RunnerBuilder
{
	RunnerBuilder builder=null;
	
	public MyRunnerBuilder(RunnerBuilder builder)
	{
		this.builder=builder;
	}
	
	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable
	{
		return getRunner(builder, testClass);
	}
	
	/**
	 * Create a single-class runner for {@code testClass}, using {@code builder}
	 */
	protected Runner getRunner(RunnerBuilder builder, Class<?> testClass) throws Throwable 
	{
		return builder.runnerForClass(testClass);
	}
}
