package org.mearvk;

import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

/**
 * A builder of ordered test runners; used to fit within the existing 4.10 framework.
 * 
 * @see http://code.google.com/p/junit-test-orderer/ for licensing questions.
 * 
 * @author Max Rupplin
 */
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
