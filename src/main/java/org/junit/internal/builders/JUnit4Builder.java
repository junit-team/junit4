/**
 * 
 */
package org.junit.internal.builders;

import org.junit.internal.runners.BlockJUnit4ClassRunner;
import org.junit.runner.Runner;
import org.junit.runners.RunnerBuilder;

public class JUnit4Builder extends RunnerBuilder {
	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable {
		return new BlockJUnit4ClassRunner(testClass);
	}
}