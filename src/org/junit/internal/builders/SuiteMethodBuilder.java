/**
 * 
 */
package org.junit.internal.builders;

import org.junit.internal.runners.SuiteMethod;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

public class SuiteMethodBuilder extends RunnerBuilder {
	@Override
	public Runner runnerForClass(Class<?> each) throws Throwable {
		if (hasSuiteMethod(each))
			return new SuiteMethod(each);
		return null;
	}

	public boolean hasSuiteMethod(Class<?> testClass) {
		try {
			testClass.getMethod("suite");
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}
}