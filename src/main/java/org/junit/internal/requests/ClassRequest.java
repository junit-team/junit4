package org.junit.internal.requests;


import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class ClassRequest extends Request {
	private final Class<?> fTestClass;

	private boolean fCanUseSuiteMethod;

	public ClassRequest(Class<?> testClass, boolean canUseSuiteMethod) {
		fTestClass= testClass;
		fCanUseSuiteMethod= canUseSuiteMethod;
	}

	public ClassRequest(Class<?> testClass) {
		this(testClass, true);
	}

	@Override
	public Runner getRunner() {
		return new AllDefaultPossibilitiesBuilder(fCanUseSuiteMethod).safeRunnerForClass(fTestClass);
	}
}