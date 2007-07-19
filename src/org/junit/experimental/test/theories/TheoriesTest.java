package org.junit.experimental.test.theories;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import static org.junit.matchers.StringContains.containsString;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
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

	public static int INDEPENDENCE= 1776;

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
		assertThat(testResult(HasATheory.class),
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
		assertThat(testResult(HasATwoParameterTheory.class), ResultMatchers
				.isSuccessful());
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
		assertThat(testResult(DoesntUseParams.class),
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
		assertThat(testResult(NullsOK.class),
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
		assertThat(testResult(ParameterAnnotations.class), ResultMatchers
				.isSuccessful());
	}

	@RunWith(Theories.class)
	public static class HonorExpectedException {
		@Test(expected= NullPointerException.class)
		public void shouldThrow() {

		}
	}

	@Test
	public void honorExpected() throws Exception {
		assertThat(testResult(HonorExpectedException.class).getFailures()
				.size(), is(1));
	}

	@RunWith(Theories.class)
	public static class HonorTimeout {
		@Test(timeout= 5)
		public void shouldStop() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
			}
		}
	}

	@Test
	public void honorTimeout() throws Exception {
		assertThat(testResult(HonorTimeout.class), failureCountIs(1));
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

		String result= testResult(AssumptionsFail.class).toString();
		assertThat(result, containsString(matcher.toString()));
		assertThat(result, containsString("" + data));
	}

	@RunWith(Theories.class)
	public static class ShouldFilterNull {
		@DataPoint
		public static String NULL= null;

		@DataPoint
		public static String A= "a";

		@Theory(nullsAccepted= false)
		public void allStringsAreNonNull(String s) {
			assertThat(s, notNullValue());
		}
	}

	@Test
	public void shouldFilterNull() {
		assertThat(testResult(ShouldFilterNull.class), isSuccessful());
	}
}
