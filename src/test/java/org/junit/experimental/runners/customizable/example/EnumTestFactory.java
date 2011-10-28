package org.junit.experimental.runners.customizable.example;

import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.runners.customizable.FrameworkTest;
import org.junit.experimental.runners.customizable.TestFactory;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class EnumTestFactory implements TestFactory {

	public List<FrameworkTest> computeTestMethods(TestClass testClass,
			List<Throwable> errors) {
		List<FrameworkTest> tests= new ArrayList<FrameworkTest>();

		List<FrameworkMethod> methods= testClass
				.getAnnotatedMethods(EnumTest.class);
		for (FrameworkMethod method : methods) {
			EnumTest annotation= method.getAnnotation(EnumTest.class);
			for (Object value : annotation.enumType().getEnumConstants()) {
				tests.add(new EnumFrameworkTest(this, testClass, method, value));
			}
			if (annotation.nullable()) {
				tests.add(new EnumFrameworkTest(this, testClass, method, null));
			}
		}

		return tests;
	}
}

class EnumFrameworkTest implements FrameworkTest {

	private final FrameworkMethod frameworkMethod;

	private final Object value;

	private final Description description;

	public EnumFrameworkTest(EnumTestFactory factory, TestClass testClass,
			FrameworkMethod frameworkMethod, Object value) {
		this.frameworkMethod= frameworkMethod;
		this.value= value;

		description= Description.createTestDescription(
				testClass.getJavaClass(), frameworkMethod.getName() + "["
						+ value + "]", frameworkMethod.getAnnotations());
	}

	public Description createDescription() {
		return description;
	}

	public boolean isIgnored() {
		return false;
	}

	public Statement createStatement(final Object testInstance,
			List<TestRule> testRules) {
		Statement statement= new Statement() {

			@Override
			public void evaluate() throws Throwable {
				frameworkMethod.invokeExplosively(testInstance, value);
			}
		};
		for (TestRule testRule : testRules) {
			statement= testRule.apply(statement, description);
		}
		return statement;
	}
}
