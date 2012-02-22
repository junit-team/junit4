package org.junit.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.MethodRunOrder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class OrderedTestRunner extends Runner
{
	private Class<?> testClass=null;
	
	public OrderedTestRunner(Class<?> testClass) throws InitializationError
	{
		this.testClass = testClass;

        Order
	}

	@Override
	public Description getDescription()
	{
		return Description.createSuiteDescription(testClass);
	}

	@Override
	public void run(RunNotifier notifier)
	{
		notifier.fireTestStarted(getDescription());
		
		System.err.println("OrderedTestRunner should be running the tests now...");
		
		try
		{						
			for(Method meth : testClass.getDeclaredMethods()) //for each declared method
			for(Annotation anno : meth.getAnnotations()) //for each of its annotations
			{
				if( anno instanceof MethodRunOrder )
				{
					System.err.println("About to run "+testClass.getSimpleName()+"."+meth.getName());
					
					try
					{						
						Object retval = meth.invoke(testClass.newInstance(), (Object[])null);
					}
					catch (Exception e)
					{
						//notifier.fireTestAssumptionFailed(new Failure(getDescription(), e));
						System.err.println(e);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		notifier.fireTestFinished(getDescription());
	}
}
