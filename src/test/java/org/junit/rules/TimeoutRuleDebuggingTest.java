package org.junit.rules;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TimeoutRuleDebuggingTest {

    /**
     * Used to allow tests to manipulate when we are in debug mode
     */
    private final static class TestTimeout extends Timeout {
        static List<String> arguments;

        private TestTimeout(boolean enabledOnDebug) {
            super(1, TimeUnit.MILLISECONDS, false, enabledOnDebug);
        }

        @Override
        boolean isDebugging() {
            return isDebugging(arguments);
        }
    }

    public static class TimeoutEnabledOnDebugTest {
        @Rule
        public Timeout timeout = new TestTimeout(true);

        @Test
        public void test() throws InterruptedException {
            Thread.sleep(200);
        }
    }

    @Test
    public void whenTimeoutEnabledOnDebug_andDebuggingWithPreJava5Options_expectTimeoutFires() {
        JUnitCore core = new JUnitCore();
        TestTimeout.arguments = Arrays.asList("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=8000");
        Result result = core.run(TimeoutEnabledOnDebugTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should have failed", 1, result.getFailureCount());
    }

    @Test
    public void whenTimeoutEnabledOnDebug_andDebuggingWithPostJava5Option_expectTimeoutFires() {
        JUnitCore core = new JUnitCore();
        TestTimeout.arguments = Arrays.asList("-agentlib:jdwp=transport=dt_socket,server=y,address=8000");
        Result result = core.run(TimeoutEnabledOnDebugTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should have failed", 1, result.getFailureCount());
    }

    @Test
    public void whenTimeoutEnabledOnDebug_andNormalExecution_expectTimeoutFires() {
        JUnitCore core = new JUnitCore();
        TestTimeout.arguments = Collections.emptyList();
        Result result = core.run(TimeoutEnabledOnDebugTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should have failed", 1, result.getFailureCount());
    }

    public static class TimeoutDisabledOnDebugTest {
        @Rule
        public Timeout timeout = new TestTimeout(false);

        @Test
        public void test() throws InterruptedException {
            Thread.sleep(200);
        }
    }

    @Test
    public void whenTimeoutDisabledOnDebug_andDebuggingWithPreJava5Options_expectTimeoutDoesNotFire() {
        JUnitCore core = new JUnitCore();
        TestTimeout.arguments = Arrays.asList("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=8000");
        Result result = core.run(TimeoutDisabledOnDebugTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should not have failed", 0, result.getFailureCount());
    }

    @Test
    public void whenTimeoutDisabledOnDebug_andDebuggingWithPostJava5Options_expectTimeoutDoesNotFire() {
        JUnitCore core = new JUnitCore();
        TestTimeout.arguments = Arrays.asList("-agentlib:jdwp=transport=dt_socket,server=y,address=8000");
        Result result = core.run(TimeoutDisabledOnDebugTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should not have failed", 0, result.getFailureCount());
    }


    @Test
    public void whenTimeoutDisabledOnDebug_andNormalExecution_expectTimeoutFires() {
        JUnitCore core = new JUnitCore();
        TestTimeout.arguments = Collections.emptyList();
        Result result = core.run(TimeoutDisabledOnDebugTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should have failed", 1, result.getFailureCount());
    }

}
