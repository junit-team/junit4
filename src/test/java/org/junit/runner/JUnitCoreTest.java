package org.junit.runner;

import org.junit.Test;
import org.junit.tests.TestSystem;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JUnitCoreTest {
    @Test
    public void shouldAddFailuresToResult() {
        JUnitCore jUnitCore = new JUnitCore();

        Result result = jUnitCore.runMain(new TestSystem(), "NonExistentTest");

        assertThat(result.getFailureCount(), is(1));
        assertThat(result.getFailures().get(0).getException(), instanceOf(IllegalArgumentException.class));
    }
}
