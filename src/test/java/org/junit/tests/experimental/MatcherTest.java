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
	public static Matcher<?> SINGLE_FAILURE= hasSingleFailureContaining("cheese");

	@DataPoint
	public static Matcher<?> ANY_FAILURE= hasFailureContaining("cheese");

	// TODO: (Dec 7, 2007 1:25:37 PM) DUP

	@DataPoint
	public static PrintableResult TWO_FAILURES_ONE_CHEESE= new PrintableResult(
			Arrays.asList(new Failure(Description.EMPTY, new Error("cheese")),
					new Failure(Description.EMPTY, new Error("mustard"))));

	@Theory
	public <T> void differentMatchersHaveDifferentDescriptions(
			Matcher<T> matcher1, Matcher<T> matcher2, T value) {
		assumeThat(value, matcher1);
		assumeThat(value, not(matcher2));
		assertThat(matcher1.toString(), not(matcher2.toString()));
	}

}
