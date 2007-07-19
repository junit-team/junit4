package org.junit.tests;

import static org.junit.matchers.StringContains.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class AssumptionViolatedExceptionTest {
	public static Object TWO= 2;

	public static Matcher<?> IS_THREE= is(3);

	@Theory
	public void toStringIsUseful(Object actual, Matcher<?> matcher) {
		assertThat(new AssumptionViolatedException(actual, matcher).toString(),
				containsString(matcher.toString()));
	}

	@Test
	public void AssumptionViolatedExceptionDescribesItself() {
		AssumptionViolatedException e= new AssumptionViolatedException(3, is(2));
		assertThat(StringDescription.asString(e), is("got: <3>, expected: is <2>"));
	}
}
