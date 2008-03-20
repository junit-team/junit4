/**
 *
 */
package org.junit.internal.builders;

import java.util.Arrays;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.RunnerBuilder;

public class AllDefaultPossibilitiesBuilder extends RunnerBuilder {
	private final boolean fCanUseSuiteMethod;

	public AllDefaultPossibilitiesBuilder(boolean canUseSuiteMethod) {
		fCanUseSuiteMethod= canUseSuiteMethod;
	}

	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable {
		List<RunnerBuilder> builders= Arrays.asList(
				new IgnoredBuilder(),
				new AnnotatedBuilder(this),
				suiteMethodBuilder(),
				new JUnit3Builder(),
				new JUnit4Builder());

		for (RunnerBuilder each : builders) {
			Runner runner= each.safeRunnerForClass(testClass);
			if (runner != null)
				return runner;
		}
		return null;
	}

	private RunnerBuilder suiteMethodBuilder() {
		if (fCanUseSuiteMethod)
			return new SuiteMethodBuilder();
		return new NullBuilder();
	}
}