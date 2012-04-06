package org.junit.test;

import static org.junit.Assert.assertTrue;
import static org.junit.test.DescriptionMatchers.hasDisplayName;
import static org.junit.test.EventCollectorMatchers.finishedTest;
import static org.junit.test.EventCollectorMatchers.noFailures;
import static org.junit.test.EventCollectorMatchers.numberOfFailures;
import static org.junit.test.EventCollectorMatchers.numberOfStartedTests;
import static org.junit.test.EventCollectorMatchers.onlySuccessfulTests;
import static org.junit.test.EventCollectorMatchers.startedTest;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitSelfTestRunner.class)
public class JUnitSelfTestRunnerTest {
	@JUnitSelfTest
	@RunWith(JUnitSelfTestRunner.class)
	public static class SuccessfulSelfTest {
		@ExpectedEvents
		public static final Matcher<EventCollector> EXPECTED_EVENTS= onlySuccessfulTests();

		@JUnitSelfTest
		public static class FirstSelfTest {
			@ExpectedEvents
			public static final Matcher<EventCollector> EXPECTED_EVENTS= onlySuccessfulTests();

			@Test
			public void successfulTest() {
				assertTrue(true);
			}
		}
	}

	@JUnitSelfTest
	@RunWith(JUnitSelfTestRunner.class)
	public static class TwoFailingSelfTests {
		@ExpectedEvents
		public static final Matcher<EventCollector> EXPECTED_EVENTS= noFailures();

		@JUnitSelfTest
		public static class SelfTest {
			@ExpectedEvents
			public static final Matcher<EventCollector> EXPECTED_EVENTS= numberOfFailures(0);

			@Test
			public void successfulTest() {
				assertTrue(true);
			}
		}
	}

	@JUnitSelfTest
	@RunWith(JUnitSelfTestRunner.class)
	public static class UseClassNameAsTestName {
		@ExpectedEvents
		public static final Matcher<EventCollector> EXPECTED_EVENTS= startedTest(hasDisplayName("SelfTest(org.junit.test.JUnitSelfTestRunnerTest$UseClassNameAsTestName)"));

		@JUnitSelfTest
		public static class SelfTest {
			@ExpectedEvents
			public static final Matcher<EventCollector> EXPECTED_EVENTS= onlySuccessfulTests();

			@Test
			public void successfulTest() {
				assertTrue(true);
			}
		}
	}

	@JUnitSelfTest
	@RunWith(JUnitSelfTestRunner.class)
	public static class CreateFinishedEvent {
		@ExpectedEvents
		public static final Matcher<EventCollector> EXPECTED_EVENTS= finishedTest(hasDisplayName("SelfTest(org.junit.test.JUnitSelfTestRunnerTest$CreateFinishedEvent)"));

		@JUnitSelfTest
		public static class SelfTest {
			@ExpectedEvents
			public static final Matcher<EventCollector> EXPECTED_EVENTS= onlySuccessfulTests();

			@Test
			public void successfulTest() {
				assertTrue(true);
			}
		}
	}

	@JUnitSelfTest
	@RunWith(JUnitSelfTestRunner.class)
	public static class FailWithoutExpectedEvents {
		@ExpectedEvents
		public static final Matcher<EventCollector> EXPECTED_EVENTS= numberOfFailures(1);

		@JUnitSelfTest
		public static class SelfTest {
			@Test
			public void successfulTest() {
				assertTrue(true);
			}
		}
	}

	@JUnitSelfTest
	@RunWith(JUnitSelfTestRunner.class)
	public static class FailWithMoreThanOneExpectedEvents {
		@ExpectedEvents
		public static final Matcher<EventCollector> EXPECTED_EVENTS= numberOfFailures(1);

		@JUnitSelfTest
		public static class SelfTest {
			@ExpectedEvents
			public static final Matcher<EventCollector> FIRST_EXPECTED_EVENTS= noFailures();

			@ExpectedEvents
			public static final Matcher<EventCollector> SECOND_EXPECTED_EVENTS= noFailures();

			@Test
			public void successfulTest() {
				assertTrue(true);
			}
		}
	}

	@JUnitSelfTest
	@RunWith(JUnitSelfTestRunner.class)
	public static class DontConsiderNonAnnotatedClass {
		@ExpectedEvents
		public static final Matcher<EventCollector> EXPECTED_EVENTS= numberOfStartedTests(1);

		public static class IrrelevantClass {
		}

		@JUnitSelfTest
		public static class SelfTest {
			@ExpectedEvents
			public static final Matcher<EventCollector> FIRST_EXPECTED_EVENTS= noFailures();

			@Test
			public void successfulTest() {
				assertTrue(true);
			}
		}
	}
}
