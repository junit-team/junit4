package org.junit.tests.running.classes;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.tests.experimental.rules.RuleFieldValidatorTest.TestWithNonStaticClassRule;
import org.junit.tests.experimental.rules.RuleFieldValidatorTest.TestWithProtectedClassRule;

public class ParentRunnerTest {
	public static String log= "";

	public static class FruitTest {
		@Test
		public void apple() {
			log+= "apple ";
		}

		@Test
		public void banana() {
			log+= "banana ";
		}
	}

	@Test
	public void useChildHarvester() throws InitializationError {
		log= "";
		ParentRunner<?> runner= new BlockJUnit4ClassRunner(FruitTest.class);
		runner.setScheduler(new RunnerScheduler() {
			public void schedule(Runnable childStatement) {
				log+= "before ";
				childStatement.run();
				log+= "after ";
			}

			public void finished() {
				log+= "afterAll ";
			}
		});

		runner.run(new RunNotifier());
		assertEquals("before apple after before banana after afterAll ", log);
	}

	@Test
	public void testMultipleFilters() throws Exception {
		JUnitCore junitCore= new JUnitCore();
		Request request= Request.aClass(ExampleTest.class);
		Request requestFiltered= request.filterWith(new Exclude("test1"));
		Request requestFilteredFiltered= requestFiltered
				.filterWith(new Exclude("test2"));
		Result result= junitCore.run(requestFilteredFiltered);
		assertThat(result.getFailures(), isEmpty());
		assertEquals(1, result.getRunCount());
	}

	private Matcher<List<?>> isEmpty() {
		return new TypeSafeMatcher<List<?>>() {
			public void describeTo(org.hamcrest.Description description) {
				description.appendText("is empty");
			}

			@Override
			public boolean matchesSafely(List<?> item) {
				return item.size() == 0;
			}
		};
	}

	private static class Exclude extends Filter {
		private String methodName;

		public Exclude(String methodName) {
			this.methodName= methodName;
		}

		@Override
		public boolean shouldRun(Description description) {
			return !description.getMethodName().equals(methodName);
		}

		@Override
		public String describe() {
			return "filter method name: " + methodName;
		}
	}

	public static class ExampleTest {
		@Test
		public void test1() throws Exception {
		}

		@Test
		public void test2() throws Exception {
		}

		@Test
		public void test3() throws Exception {
		}
	}

	@Test
	public void failWithHelpfulMessageForProtectedClassRule() {
		assertClassHasFailureMessage(TestWithProtectedClassRule.class,
				"The @ClassRule 'temporaryFolder' must be public.");
	}

	@Test
	public void failWithHelpfulMessageForNonStaticClassRule() {
		assertClassHasFailureMessage(TestWithNonStaticClassRule.class,
				"The @ClassRule 'temporaryFolder' must be static.");
	}

	private void assertClassHasFailureMessage(Class<?> klass, String message) {
		JUnitCore junitCore= new JUnitCore();
		Request request= Request.aClass(klass);
		Result result= junitCore.run(request);
		assertThat(result.getFailureCount(), is(2)); //the second failure is no runnable methods
		assertThat(result.getFailures().get(0).getMessage(),
				is(equalTo(message)));

	}
}
