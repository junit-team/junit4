package org.junit.experimental.runners.customizable;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class CustomizableJUnit4ClassRunnerTest {

	@Test
	public void discoverTestFactories_shouldDefaultToReflectionTestFactory()
			throws InitializationError {
		new CustomizableJUnit4ClassRunner(DefaultTestClass.class) {

			@Override
			protected List<TestFactory> discoverTestFactories(Class<?> klass)
					throws InitializationError {
				List<TestFactory> testFactories= super
						.discoverTestFactories(klass);
				assertNotNull(testFactories);
				assertEquals(1, testFactories.size());
				assertEquals(ReflectionTestFactory.class, testFactories.get(0)
						.getClass());
				return testFactories;
			}
		};
	}

	@Test
	public void discoverTestFactories_shouldUseAnnotationIfProvided()
			throws InitializationError {
		new CustomizableJUnit4ClassRunner(AnnotatedTestClass.class) {

			@Override
			protected List<TestFactory> discoverTestFactories(Class<?> klass)
					throws InitializationError {
				List<TestFactory> testFactories= super
						.discoverTestFactories(klass);
				assertNotNull(testFactories);
				assertEquals(1, testFactories.size());
				assertEquals(LocalTestFactory.class, testFactories.get(0)
						.getClass());
				return testFactories;
			}
		};
	}

	@Test
	public void describeChild_shouldDelegateToFrameworkTest()
			throws InitializationError {
		final Description description= Description.createSuiteDescription("MY");
		LocalFrameworkTest localTest= new LocalFrameworkTest() {

			@Override
			public Description createDescription() {
				return description;
			}
		};
		CustomizableJUnit4ClassRunner runner= new CustomizableJUnit4ClassRunner(
				AnnotatedTestClass.class,
				Arrays.<TestFactory> asList(new LocalTestFactory(localTest)));
		Description actual= runner.describeChild(localTest);
		assertSame(description, actual);
	}

	@Test
	public void runChild_shouldConsultFrameworkTestForIgnore()
			throws InitializationError {
		final Description localDescription= Description
				.createSuiteDescription("MY");
		LocalFrameworkTest localTest= new LocalFrameworkTest() {
			@Override
			public Description createDescription() {
				return localDescription;
			}

			@Override
			public boolean isIgnored() {
				return true;
			}

		};
		CustomizableJUnit4ClassRunner runner= new CustomizableJUnit4ClassRunner(
				AnnotatedTestClass.class,
				Arrays.<TestFactory> asList(new LocalTestFactory(localTest)));
		RunNotifier notifier= new RunNotifier() {

			@Override
			public void fireTestIgnored(Description description) {
				assertSame(localDescription, description);
				throw new SUCCESS();
			}
		};
		try {
			runner.runChild(localTest, notifier);
			fail("Test was not ignored");
		} catch (SUCCESS exc) {
		}
	}

	public static class DefaultTestClass {

		@Test
		public void test() {
		}
	}

	@TestFactories({ LocalTestFactory.class })
	public static class AnnotatedTestClass {

		@Test
		public void test() {
		}
	}

	public static class LocalFrameworkTest implements FrameworkTest {

		public Description createDescription() {
			return null;
		}

		public boolean isIgnored() {
			return false;
		}

		public Statement createStatement(Object testInstance,
				List<TestRule> testRules) {
			return null;
		}

	}

	public static class LocalTestFactory implements TestFactory {
		private final LocalFrameworkTest test;

		public LocalTestFactory() {
			test= new LocalFrameworkTest();
		}

		public LocalTestFactory(LocalFrameworkTest test) {
			this.test= test;
		}

		public List<FrameworkTest> computeTestMethods(TestClass testClass,
				List<Throwable> errors) {
			return Arrays.<FrameworkTest> asList(test);
		}
	}

	private static class SUCCESS extends RuntimeException {
	}
}
