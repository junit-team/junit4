package org.mearvk;

import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

/**
 * A builder of ordered test runners; used to fit within the existing 4.10
 * framework.
 * 
 * @see <a href="http://code.google.com/p/junit-test-orderer">Licensing, code source, etc.</a>
 * 
 * @author Max Rupplin
 */
public class OrderedTestRunnerBuilder extends RunnerBuilder
{
	/**
	 * Returns a OrderedTestRunner if the class is annotated with ClassRunOrder or else null
	 */
	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable
	{
		if (testClass.isAnnotationPresent(ClassRunOrder.class))
		{
			return new OrderedTestRunner(testClass);
		}
		else return null;
	}
}
