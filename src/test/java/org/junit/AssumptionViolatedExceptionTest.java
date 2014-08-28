package org.junit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class AssumptionViolatedExceptionTest {
    @DataPoint
    public static Object TWO = 2;

    @DataPoint
    public static Matcher<?> IS_THREE = is(3);

    @DataPoint
    public static Matcher<?> NULL = null;

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
        AssumptionViolatedException e = new AssumptionViolatedException(3, is(2));
        assertThat(StringDescription.asString(e), is("got: <3>, expected: is <2>"));
    }

    @Test
    public void simpleAssumptionViolatedExceptionDescribesItself() {
        AssumptionViolatedException e = new AssumptionViolatedException("not enough money");
        assertThat(StringDescription.asString(e), is("not enough money"));
    }

    @Test
    public void nullCause() {
        AssumptionViolatedException e = new AssumptionViolatedException("invalid number");
        assertThat(e.getCause(), nullValue());
    }

    @Test
    public void nullCauseWithObjectAndMatcher() {
        Throwable testObject = new Exception();
        AssumptionViolatedException e = new AssumptionViolatedException(testObject, containsString("test matcher"));
        assertThat(e.getCause(), nullValue());
    }

    @Test
    public void nullCauseWithAssumptionObjectAndMatcher() {
        Throwable testObject = new Exception();
        AssumptionViolatedException e = new AssumptionViolatedException(
            "sample assumption", testObject, containsString("test matcher")
        );
        assertThat(e.getCause(), nullValue());
    }

    @Test
    public void nullCauseWithMainConstructor() {
        Throwable testObject = new Exception();
        AssumptionViolatedException e = new AssumptionViolatedException(
            "sample assumption", false, testObject, containsString("test matcher")
        );
        assertThat(e.getCause(), nullValue());
    }

    @Test
    public void notNullCause() {
        Throwable cause = new Exception();
        AssumptionViolatedException e = new AssumptionViolatedException("invalid number", cause);
        assertThat(e.getCause(), is(cause));
    }
}
