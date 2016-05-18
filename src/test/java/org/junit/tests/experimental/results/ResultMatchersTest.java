package org.junit.tests.experimental.results;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.Theory;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import java.util.Collections;

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
    public void testFailureCountIsTakingMatcher_givenResultWithOneFailure() {
        PrintableResult resultWithOneFailure = new PrintableResult(Collections.singletonList(
                new Failure(Description.createTestDescription("class", "name"), new RuntimeException("failure 1"))));

        assertThat(ResultMatchers.failureCountIs(equalTo(3)).matches(resultWithOneFailure), is(false));
        assertThat(ResultMatchers.failureCountIs(equalTo(1)).matches(resultWithOneFailure), is(true));
    }

    @Test
    public void failureCountIsTakingMatcherShouldHaveGoodDescription() {
        Matcher<Integer> matcherTestStub = new BaseMatcher<Integer>() {
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("STUBBED DESCRIPTION");
            }

            public boolean matches(Object item) {
                throw new UnsupportedOperationException();
            }
        };

        assertThat(ResultMatchers.failureCountIs(matcherTestStub).toString(),
                is("has a number of failures matching STUBBED DESCRIPTION"));
    }
}
