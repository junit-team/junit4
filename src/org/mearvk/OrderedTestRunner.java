package org.mearvk;

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

        OrderedSuite.registerOrderedClass(testClass);
	}

	@Override
	public Description getDescription()
	{
		return Description.createSuiteDescription(testClass);
	}

	@Override
	public void run(RunNotifier notifier)
	{
        OrderedSuite.runNext(notifier);
	}
}
