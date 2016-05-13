package org.junit.tests.experimental.results;

import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.Theory;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ResultMatchersTest {
    @Test
    public void hasFailuresHasGoodDescription() {
        assertThat(ResultMatchers.failureCountIs(3).toString(),
                is("has 3 failures"));
    }

    @Theory
    public void hasFailuresDescriptionReflectsInput(int i) {
        assertThat(ResultMatchers.failureCountIs(i).toString(),
                containsString("" + i));
    }

    @Test
    public void hasFailureContaining_givenNonMatchingScenario() {
        PrintableResult resultWithNoFailures = new PrintableResult(new ArrayList<Failure>());
        assertThat(ResultMatchers.hasFailureContaining("").matches(resultWithNoFailures), is(false));
    }

    @Test
    public void failureCount_nonMatchingScenario() {
        PrintableResult resultWithNoFailures = new PrintableResult(new ArrayList<Failure>());
        assertThat(ResultMatchers.failureCount(greaterThanOrEqualTo(3)).matches(resultWithNoFailures), is(false));
    }
}
