package org.junit.internal.builders;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import org.junit.ClassRunOrder;
import org.junit.runner.Runner;
import org.junit.runners.OrderedTestRunner;
import org.junit.runners.model.RunnerBuilder;

public class OrderedTestRunnerBuilder extends RunnerBuilder
{
	public OrderedTestRunnerBuilder()
	{
		
	}
	
	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable
	{		
        if(testClass.isAnnotationPresent(ClassRunOrder.class)) return new OrderedTestRunner(testClass);
			
		return null;
	}
}
