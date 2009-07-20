package org.junit.tests.experimental;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import static org.junit.internal.matchers.StringContains.containsString;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class AssumptionTest {
	public static class HasFailingAssumption {
		@Test
		public void assumptionsFail() {
			assumeThat(3, is(4));
			fail();
		}
	}

	@Test
	public void failedAssumptionsMeanPassing() {
		Result result= JUnitCore.runClasses(HasFailingAssumption.class);
		assertThat(result.getRunCount(), is(1));
		assertThat(result.getIgnoreCount(), is(0));
		assertThat(result.getFailureCount(), is(0));
	}

	private static int assumptionFailures= 0;
	@Test
	public void failedAssumptionsCanBeDetectedByListeners() {
		assumptionFailures= 0;
		JUnitCore core= new JUnitCore();
		core.addListener(new RunListener() {
			@Override
			public void testAssumptionFailure(Failure failure) {
				assumptionFailures++;
			}
		});
		core.run(HasFailingAssumption.class);
		
		assertThat(assumptionFailures, is(1));
	}

	public static class HasPassingAssumption {
		@Test
		public void assumptionsFail() {
			assumeThat(3, is(3));
			fail();
		}
	}

	@Test
	public void passingAssumptionsScootThrough() {
		Result result= JUnitCore.runClasses(HasPassingAssumption.class);
		assertThat(result.getRunCount(), is(1));
		assertThat(result.getIgnoreCount(), is(0));
		assertThat(result.getFailureCount(), is(1));
	}

	@Test(expected= AssumptionViolatedException.class)
	public void assumeThatWorks() {
		assumeThat(1, is(2));
	}

	@Test
	public void assumeThatPasses() {
		assumeThat(1, is(1));
		assertCompletesNormally();
	}

	@Test
	public void assumeThatPassesOnStrings() {
		assumeThat("x", is("x"));
		assertCompletesNormally();
	}

	@Test(expected= AssumptionViolatedException.class)
	public void assumeNotNullThrowsException() {
		Object[] objects= { 1, 2, null };
		assumeNotNull(objects);
	}

	@Test
	public void assumeNotNullPasses() {
		Object[] objects= { 1, 2 };
		assumeNotNull(objects);
		assertCompletesNormally();
	}

	@Test
	public void assumeNotNullIncludesParameterList() {
		try {
			Object[] objects= { 1, 2, null };
			assumeNotNull(objects);
		} catch (AssumptionViolatedException e) {
			assertThat(e.getMessage(), containsString("1, 2, null"));
		} catch (Exception e) {
			fail("Should have thrown AssumptionViolatedException");
		}
	}
	@Test
	public void assumeNoExceptionThrows() {
		final Throwable exception= new NullPointerException();
		try {
			assumeNoException(exception);
			fail("Should have thrown exception");
		} catch (AssumptionViolatedException e) {
			assertThat(e.getCause(), is(exception));
		}
	}

	private void assertCompletesNormally() {
	}

	@Test(expected=AssumptionViolatedException.class) public void assumeTrueWorks() {
		Assume.assumeTrue(false);
	}

	public static class HasFailingAssumeInBefore {
		@Before public void checkForSomethingThatIsntThere() {
			assumeTrue(false);
		}

		@Test public void failing() {
			fail();
		}
	}

	@Test public void failingAssumptionInBeforePreventsTestRun() {
		assertThat(testResult(HasFailingAssumeInBefore.class), isSuccessful());
	}

	public static class HasFailingAssumeInBeforeClass {
		@BeforeClass public static void checkForSomethingThatIsntThere() {
			assumeTrue(false);
		}

		@Test public void failing() {
			fail();
		}
	}

	@Test public void failingAssumptionInBeforeClassIgnoresClass() {
		assertThat(testResult(HasFailingAssumeInBeforeClass.class), isSuccessful());
	}

	public static class AssumptionFailureInConstructor {
		public AssumptionFailureInConstructor() {
			assumeTrue(false);
		}

		@Test public void shouldFail() {
			fail();
		}
	}

	@Test public void failingAssumptionInConstructorIgnoresClass() {
		assertThat(testResult(AssumptionFailureInConstructor.class), isSuccessful());
	}
}
