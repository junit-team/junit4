package org.junit.tests.experimental;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.internal.matchers.StringContains.containsString;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class AssumptionViolatedExceptionTest {
	@DataPoint
	public static Object TWO= 2;

	@DataPoint
	public static Matcher<?> IS_THREE= is(3);

	@DataPoint
	public static Matcher<?> NULL= null;

	@Theory
	public void toStringReportsMatcher(Object actual, Matcher<?> matcher) {
		assumeThat(matcher, notNullValue());
		assertThat(new AssumptionViolatedException(actual, matcher).toString(),
				containsString(matcher.toString()));
	}

	@Theory
	public void toStringReportsValue(Object actual, Matcher<?> matcher) {
		assertThat(new AssumptionViolatedException(actual, matcher).toString(),
				containsString(String.valueOf(actual)));
	}

	@Test
	public void AssumptionViolatedExceptionDescribesItself() {
		AssumptionViolatedException e= new AssumptionViolatedException(3, is(2));
		assertThat(StringDescription.asString(e), is("got: <3>, expected: is <2>"));
	}

	@Test
	public void simpleAssumptionViolatedExceptionDescribesItself() {
		AssumptionViolatedException e= new AssumptionViolatedException("not enough money");
		assertThat(StringDescription.asString(e), is("failed assumption: not enough money"));
	}
}
