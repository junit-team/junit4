package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.internal.matchers.StringContains.containsString;
import org.hamcrest.Matcher;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class WhenNoParametersMatch {
	@DataPoints
	public static int[] ints= { 0, 1, 3, 5, 1776 };

	@DataPoints
	public static Matcher<?>[] matchers= { not(0), is(1) };

	@RunWith(Theories.class)
	public static class AssumptionsFail {
		@DataPoint
		public static int DATA= 0;

		@DataPoint
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
}