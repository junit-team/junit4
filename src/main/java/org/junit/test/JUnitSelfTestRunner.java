package org.junit.test;

import static org.junit.Assert.assertThat;
import static org.junit.runner.Description.createTestDescription;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * The {@code JUnitSelfTestRunner} is a test runner for declarative tests of
 * JUnit itself. It runs your test classes and verifies the JUnit events
 * (failure, test started, ...) according to your specification.
 * 
 * <p>
 * A test class using this runner has several static inner classes, which are
 * annotated with {@link JUnitSelfTest}. Every such class is a single test and
 * the test specification is provided by a hamcrest matcher, which is an
 * additional field annotated with {@link ExpectedEvents}. The
 * {@link EventCollectorMatchers} class provides some matchers.
 * 
 * <p>
 * Here is an example of a test class with two tests.
 * 
 * <pre>
 * &#064;RunWith(JUnitSelfTestRunner.class)
 * public class MySelfTests {
 * 	&#064;JUnitSelfTest
 * 	public static class FirstSelfTest {
 * 		&#064;ExpectedEvents
 * 		public static final Matcher&lt;EventCollector&gt; EXPECTED_EVENTS= onlySuccessfulTests();
 * 
 * 		&#064;Test
 * 		public void successfulTest() {
 * 			assertTrue(true);
 * 		}
 * 	}
 * 
 * 	&#064;JUnitSelfTest
 * 	public static class SecondSelfTest {
 * 		&#064;ExpectedEvents
 * 		public static final Matcher&lt;EventCollector&gt; EXPECTED_EVENTS= numberOfFailures(1);
 * 
 * 		&#064;Test
 * 		public void successfulTest() {
 * 			assertTrue(true);
 * 		}
 * 
 * 		&#064;Test
 * 		public void failingTest() {
 * 			fail();
 * 		}
 * 	}
 * }
 * </pre>
 */
public class JUnitSelfTestRunner extends ParentRunner<Class<?>> {

	public JUnitSelfTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

	@Override
	protected List<Class<?>> getChildren() {
		Class<?>[] declaredClasses= getTestClass().getJavaClass()
				.getDeclaredClasses();
		return filterJUnitSelfTests(declaredClasses);
	}

	private List<Class<?>> filterJUnitSelfTests(Class<?>[] declaredClasses) {
		ArrayList<Class<?>> jUnitSelfTests= new ArrayList<Class<?>>();
		for (Class<?> each : declaredClasses)
			if (each.isAnnotationPresent(JUnitSelfTest.class))
				jUnitSelfTests.add(each);
		return jUnitSelfTests;
	}

	@Override
	protected Description describeChild(Class<?> child) {
		return createTestDescription(getTestClass().getJavaClass(),
				testname(child));
	}

	private String testname(Class<?> child) {
		return child.getSimpleName();
	}

	@Override
	protected void runChild(Class<?> child, RunNotifier notifier) {
		Description description= describeChild(child);
		notifier.fireTestStarted(description);
		EventCollector collector= collectEventsForTest(child);
		verify(description, child, collector, notifier);
		notifier.fireTestFinished(description);
	}

	private EventCollector collectEventsForTest(Class<?> test) {
		EventCollector collector= new EventCollector();
		JUnitCore core= new JUnitCore();
		core.addListener(collector);
		core.run(test);
		return collector;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void verify(Description description, Class<?> testClass,
			EventCollector collector, RunNotifier notifier) {
		List<Matcher> matchers= getMatchersOfClass(testClass);
		if (matchers.size() == 1)
			verifyErrorCollector(collector, matchers.get(0), description,
					notifier);
		else
			fireWrongNumberOfMatchers(description, notifier, matchers.size());
	}

	@SuppressWarnings("rawtypes")
	private List<Matcher> getMatchersOfClass(Class<?> testClass) {
		return new TestClass(testClass).getAnnotatedFieldValues(null,
				ExpectedEvents.class, Matcher.class);
	}

	private void verifyErrorCollector(EventCollector collector,
			Matcher<EventCollector> matcher, Description description,
			RunNotifier notifier) {
		try {
			assertThat(collector, matcher);
		} catch (AssertionError e) {
			notifier.fireTestFailure(new Failure(description, e));
		}
	}

	private void fireWrongNumberOfMatchers(Description description,
			RunNotifier notifier, int numberOfMatchers) {
		RuntimeException exception= new RuntimeException(numberOfMatchers
				+ " fields with ExpectedEvents annotation instead of one.");
		Failure failure= new Failure(description, exception);
		notifier.fireTestFailure(failure);
	}
}
