/**
 * 
 */
package org.junit.internal.builders;

import org.junit.Ignore;
import org.junit.internal.IgnoreUtil;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

public class IgnoredBuilder extends RunnerBuilder {
	@Override
	public Runner runnerForClass(Class<?> testClass) {
		if (IgnoreUtil.isIgnored(Description.createSuiteDescription(testClass)))
			return new IgnoredClassRunner(testClass);
		return null;
	}
}