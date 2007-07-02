package org.junit.experimental.theories.test.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.methods.api.TestedOn;
import org.junit.experimental.theories.methods.api.Theory;
import org.junit.experimental.theories.runner.api.Theories;
import org.junit.internal.runners.TestClass;
import org.junit.runner.RunWith;

@SuppressWarnings("restriction")
@RunWith(Theories.class)
public class TheoriesTest {
	public static String SPACE= " ";

	public static String J= "J";

	public static String NULL_STRING= null;

	public static int ZERO= 0;

	public static int ONE= 1;

	public static int THREE= 3;

	public static int FIVE= 5;

	public static int INDEPENDENCE = 1776;
	
	public static Matcher<Integer> NOT_ZERO= not(0);

	public static Matcher<Integer> IS_ONE= is(1);


	@RunWith(Theories.class)
	public static class HasATheory {
		public static int ONE= 1;

		@Theory
		public void everythingIsZero(int x) {
			assertThat(x, is(0));
		}
	}

	@Test
	public void theoryClassMethodsShowUp() throws Exception {
		assertThat(new Theories(HasATheory.class).getDescription()
				.getChildren().size(), is(1));
	}

	@Test
	public void theoryAnnotationsAreRetained() throws Exception {
		assertThat(new TestClass(HasATheory.class).getAnnotatedMethods(
				Theory.class).size(), is(1));
	}

	@Test
	public void canRunTheories() throws Exception {
		assertThat(PrintableResult.testResult(HasATheory.class),
				hasSingleFailureContaining("Expected"));
	}
	
	@RunWith(Theories.class)
	public static class HasATwoParameterTheory {
		public static int ONE= 1;

		@Theory
		public void everythingIsZero(int x, int y) {
			assertThat(x, is(y));
		}
	}

	@Test
	public void canRunTwoParameterTheories() throws Exception {
		assertThat(PrintableResult.testResult(HasATwoParameterTheory.class),
				ResultMatchers.isSuccessful());
	}
	
	@RunWith(Theories.class)
	public static class DoesntUseParams {
		public static int ONE= 1;

		@Theory
		public void everythingIsZero(int x, int y) {
			assertThat(2, is(3));
		}
	}

	@Test
	public void reportBadParams() throws Exception {
		assertThat(PrintableResult.testResult(DoesntUseParams.class),
				hasSingleFailureContaining("everythingIsZero(1, 1)"));
	}

	@RunWith(Theories.class)
	public static class NullsOK {
		public static String NULL= null;

		public static String A= "A";

		@Theory
		public void everythingIsA(String a) {
			assertThat(a, is("A"));
		}
	}

	@Test
	public void nullsUsedUnlessProhibited() throws Exception {
		assertThat(PrintableResult.testResult(NullsOK.class),
				hasSingleFailureContaining("null"));
	}

	@RunWith(Theories.class)
	public static class ParameterAnnotations {
		@Theory
		public void everythingIsOne(@TestedOn(ints= { 1 })
		int number) {
			assertThat(number, is(1));
		}
	}

	@Test
	public void testedOnLimitsParameters() throws Exception {
		assertThat(PrintableResult.testResult(ParameterAnnotations.class),
				ResultMatchers.isSuccessful());
	}

	@RunWith(Theories.class)
	public static class HonorExpectedException {
		@Test(expected= NullPointerException.class)
		public void shouldThrow() {

		}
	}
	
	@Test
	public void honorExpected() throws Exception {
		assertThat(PrintableResult.testResult(HonorExpectedException.class)
				.getFailures().size(), is(1));
	}


	@RunWith(Theories.class)
	public static class HonorTimeout {
		@Test(timeout = 5)
		public void shouldStop() throws InterruptedException {
			while(true) {
				Thread.sleep(1000);
			}
		}
	}
	
	@Test
	public void honorTimeout() throws Exception {
		assertThat(PrintableResult.testResult(HonorTimeout.class)
				.getFailures().size(), is(1));
	}
	
	@RunWith(Theories.class)
	public static class AssumptionsFail {
		public static int DATA= 0;

		public static Matcher<Integer> MATCHER= null;

		@Theory
		public void nonZeroIntsAreFun(int x) {
			assumeThat(x, MATCHER);
		}
	}

	@Theory
	public void showFailedAssumptionsWhenNoParametersFound(int data,
			Matcher<Integer> matcher) throws Exception {
		assumeThat(data, not(matcher));
		AssumptionsFail.DATA= data;
		AssumptionsFail.MATCHER= matcher;

		String result= PrintableResult.testResult(AssumptionsFail.class)
				.toString();
		assertThat(result, containsString(matcher.toString()));
		assertThat(result, containsString("" + data));
	}
}
