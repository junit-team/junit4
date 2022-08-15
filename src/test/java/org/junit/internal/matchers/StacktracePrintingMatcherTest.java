package org.junit.internal.matchers;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.internal.matchers.StacktracePrintingMatcher.isException;
import static org.junit.internal.matchers.StacktracePrintingMatcher.isThrowable;

import org.junit.Test;

public class StacktracePrintingMatcherTest {

    @Test
    public void succeedsWhenInnerMatcherSucceeds() throws Exception {
        assertTrue(isThrowable(any(Throwable.class)).matches(new Exception()));
    }

    @Test
    public void failsWhenInnerMatcherFails() throws Exception {
        assertFalse(isException(notNullValue(Exception.class)).matches(null));
    }

    @Test
    public void assertThatIncludesStacktrace() {
        Exception actual = new IllegalArgumentException("my message");
        Exception expected = new NullPointerException();

        try {
            assertThat(actual, isThrowable(equalTo(expected)));
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("Stacktrace was: java.lang.IllegalArgumentException: my message"));
        }
    }
}
