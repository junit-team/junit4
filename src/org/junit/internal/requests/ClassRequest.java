package org.junit.internal.requests;

import java.lang.reflect.Constructor;

import org.junit.internal.runners.OldTestClassRunner;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.AllTests;

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
		Class<? extends Runner> runnerClass= getRunnerClass(fTestClass);
		try {
			Constructor<? extends Runner> constructor= runnerClass.getConstructor(Class.class); // TODO good error message if no such constructor
			return constructor.newInstance(new Object[] { fTestClass });
		} catch (StackOverflowError e) {
			throw new RuntimeException();
		} catch (Exception e) {
			return Request.errorReport(fTestClass, e).getRunner();
		} 
	}

	Class<? extends Runner> getRunnerClass(Class<?> testClass) {
		RunWith annotation= testClass.getAnnotation(RunWith.class);
		if (annotation != null) {
			return annotation.value();
		} else if (hasSuiteMethod() && fCanUseSuiteMethod) {
			return AllTests.class;
		} else if (isPre4Test(testClass)) {
			return OldTestClassRunner.class; 
		} else {
			return TestClassRunner.class;
		}
	}
	
	private boolean hasSuiteMethod() {
		// TODO: check all attributes
		try {
			fTestClass.getMethod("suite");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}

	boolean isPre4Test(Class<?> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
	}
}