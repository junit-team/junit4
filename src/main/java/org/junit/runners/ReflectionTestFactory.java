package org.junit.runners;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.model.AnnotatedFrameworkTest;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.FrameworkTest;
import org.junit.runners.model.TestClass;

public class ReflectionTestFactory implements TestFactory {

	public List<? extends FrameworkTest> computeTestMethods(
			TestClass testClass, List<Throwable> errors) {
		List<AnnotatedFrameworkTest> result= new ArrayList<AnnotatedFrameworkTest>();

		for (FrameworkMethod eachTestMethod : testClass
				.getAnnotatedMethods(Test.class)) {
			if (errors != null) {
				eachTestMethod.validatePublicVoidNoArg(false, errors);
			}
			result.add(new AnnotatedFrameworkTest(eachTestMethod));
		}

		return result;
	}
}
