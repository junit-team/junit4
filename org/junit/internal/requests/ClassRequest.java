package org.junit.internal.requests;

import java.lang.reflect.Constructor;

import org.junit.internal.runners.OldTestClassRunner;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class ClassRequest extends Request {
	private final Class<?> fTestClass;

	public ClassRequest(Class<?> each) {
		fTestClass= each;
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
		} else if (isPre4Test(testClass)) {
			return OldTestClassRunner.class; 
		} else {
			return TestClassRunner.class;
		}
	}
	
	boolean isPre4Test(Class<?> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
	}
}