package org.junit.tests.experimental;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;

import java.util.Arrays;

import org.hamcrest.Matcher;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@RunWith(Theories.class)
public class MatcherTest {
    @DataPoint
    public static Matcher<Object> SINGLE_FAILURE = hasSingleFailureContaining("cheese");

    @DataPoint
    public static Matcher<PrintableResult> ANY_FAILURE = hasFailureContaining("cheese");

    @DataPoint
    public static PrintableResult TWO_FAILURES_ONE_CHEESE = new PrintableResult(
            Arrays.asList(failure("cheese"), failure("mustard")));

    @Theory
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void differentMatchersHaveDifferentDescriptions(
            Matcher matcher1, Matcher matcher2, Object value) {
        assumeThat(value, matcher1);
        assumeThat(value, not(matcher2));
        assertThat(matcher1.toString(), not(matcher2.toString()));
    }

    private static Failure failure(String string) {
        return new Failure(Description.EMPTY, new Error(string));
    }
}
