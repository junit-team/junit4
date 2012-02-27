package org.mearvk;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * A runner that runs specially annotated (@ClassRunOrder, @MethodRunOrder)
 * classes. This allows testers to specify ordering in their test suites.
 * 
 * @see "http://code.google.com/p/junit-test-orderer/"
 * 
 * @author Max Rupplin
 */
public class OrderedTestRunner extends Runner
{
	private Class<?> testClass = null;

	public OrderedTestRunner(Class<?> testClass)
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
