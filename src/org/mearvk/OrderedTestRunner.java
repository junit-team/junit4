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
	protected Class<?> testClass = null;

	/**
	 * Constructor 
	 * 
	 * @param testClass Takes the class to check for certain annotations
	 */
	public OrderedTestRunner(Class<?> testClass)
	{
		this.testClass = testClass;

		//register this class to be run in the OrderedSuite
		OrderedSuite.registerOrderedClass(testClass);
	}

	/**
	 * Returns information about the class
	 */
	@Override
	public Description getDescription()
	{
		return Description.createSuiteDescription(testClass);
	}

	/**
	 * Pushes running responsibility to OrderedSuite
	 */
	@Override
	public void run(RunNotifier notifier)
	{
		//push responsibility to OrderedSuite
		OrderedSuite.runNext(notifier);
	}
}
