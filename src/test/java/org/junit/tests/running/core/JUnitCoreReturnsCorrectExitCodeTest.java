package org.junit.tests.running.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class JUnitCoreReturnsCorrectExitCodeTest {

    static public class Fail {
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

    static public class Succeed {
        @Test
        public void peacefulSilence() {
        }
    }

    @Test
    public void successCausesExitCodeOf0() throws Exception {
        runClass(getClass().getName() + "$Succeed", 0);
    }

    private void runClass(final String className, int returnCode) {
        Integer exitValue = new MainRunner().runWithCheckForSystemExit(new Runnable() {
            public void run() {
                JUnitCore.main(className);
            }
        });
        assertEquals(Integer.valueOf(returnCode), exitValue);
    }
}
