package org.junit.tests.running.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.MainRunner;

public class JUnitCoreReturnsCorrectExitCodeTest {

    public static class Fail {
        @Test
        public void kaboom() {
            fail();
        }
    }

    @Test
    public void failureCausesExitCodeOf1() throws Exception {
        runClass(getClass().getName() + "$Fail", 1);
    }

    @Test
    public void missingClassCausesExitCodeOf1() throws Exception {
        runClass("Foo", 1);
    }

    public static class Succeed {
        @Test
        public void peacefulSilence() {
        }
    }

    @Test
    public void successCausesExitCodeOf0() throws Exception {
        runClass(getClass().getName() + "$Succeed", 0);
    }

    private void runClass(final String className, int returnCode) {
        int exitValue = MainRunner.runMain(className);
        assertEquals(returnCode, exitValue);
    }
}
