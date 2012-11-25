package org.junit.tests.experimental.results;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.Theory;

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
}
