package org.mearvk;

import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

public class OrderedTestRunnerBuilder extends RunnerBuilder
{
	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable
	{		
        if(testClass.isAnnotationPresent(ClassRunOrder.class))
        {
        	return new OrderedTestRunner(testClass);
        }		
        else return null;
	}
}
