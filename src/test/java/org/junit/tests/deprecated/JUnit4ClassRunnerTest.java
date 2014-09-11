package org.junit.tests.deprecated;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

/**
 * @deprecated This is a simple smoke test to make sure the old JUnit4ClassRunner basically works.
 *             Delete this test when JUnit4ClassRunner goes to the Great Heap In The Sky.
 */
@Deprecated
public class JUnit4ClassRunnerTest {

    @SuppressWarnings("deprecation")
    @RunWith(JUnit4ClassRunner.class)
    public static class Example {
        @Test
        public void success() {
        }

        @Test
        public void failure() {
            fail();
        }
    }

    @Test
    public void runWithOldJUnit4ClassRunner() {
        Result result = JUnitCore.runClasses(Example.class);
        assertThat(result.getRunCount(), is(2));
        assertThat(result.getFailureCount(), is(1));
    }

    @SuppressWarnings("deprecation")
    @RunWith(JUnit4ClassRunner.class)
    public static class UnconstructableExample {
        public UnconstructableExample() {
            throw new UnsupportedOperationException();
        }

        @Test
        public void success() {
        }

        @Test
        public void failure() {
            fail();
        }
    }


    @Test
    public void runWithOldJUnit4ClassRunnerAndBadConstructor() {
        Result result = JUnitCore.runClasses(UnconstructableExample.class);
        assertThat(result.getRunCount(), is(2));
        assertThat(result.getFailureCount(), is(2));
    }
}
