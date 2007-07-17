package org.junit.internal.requests;

import org.junit.Ignore;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.AllTests;

public class ClassRequest extends Request {
	private static final String CONSTRUCTOR_ERROR_FORMAT= "Custom runner class %s should have a public constructor with signature %s(Class testClass)";
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
		return buildRunner(getRunnerClass(fTestClass)); 
	}

	public Runner buildRunner(Class<? extends Runner> runnerClass) {
		try {
			return runnerClass.getConstructor(Class.class).newInstance(new Object[] { fTestClass });
		} catch (NoSuchMethodException e) {
			String simpleName= runnerClass.getSimpleName();
			InitializationError error= new InitializationError(String.format(
					CONSTRUCTOR_ERROR_FORMAT, simpleName, simpleName));
			return Request.errorReport(fTestClass, error).getRunner();
		} catch (Exception e) {
			return Request.errorReport(fTestClass, e).getRunner();
		}
	}

	Class<? extends Runner> getRunnerClass(final Class<?> testClass) {
		if (testClass.getAnnotation(Ignore.class) != null)
			return new IgnoredClassRunner(testClass).getClass();
		RunWith annotation= testClass.getAnnotation(RunWith.class);
		if (annotation != null) {
			return annotation.value();
		} else if (hasSuiteMethod() && fCanUseSuiteMethod) {
			return AllTests.class;
		} else if (isPre4Test(testClass)) {
			return JUnit38ClassRunner.class; 
		} else {
			return JUnit4ClassRunner.class;
		}
	}
	
	public boolean hasSuiteMethod() {
		try {
			fTestClass.getMethod("suite");
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}

	boolean isPre4Test(Class<?> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
	}
}