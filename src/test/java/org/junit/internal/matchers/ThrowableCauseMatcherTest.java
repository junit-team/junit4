package org.junit.internal.matchers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.internal.matchers.ThrowableCauseMatcher.hasCause;

public class ThrowableCauseMatcherTest {

    @Test
    public void shouldAllowCauseOfDifferentClassFromRoot() throws Exception {
        NullPointerException expectedCause = new NullPointerException("expected");
        Exception actual = new Exception(expectedCause);

        assertThat(actual, hasCause(is(expectedCause)));
    }
}