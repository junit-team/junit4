package org.junit.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;

public class DisableOnDebugTest {
    private static final List<String> WITHOUT_DEBUG_ARGUMENTS = Collections
            .emptyList();

    private static final List<String> PRE_JAVA5_DEBUG_ARGUMENTS = Arrays
            .asList("-Xdebug",
                    "-Xrunjdwp:transport=dt_socket,server=y,address=8000");

    private static final List<String> PRE_JAVA5_DEBUG_ARGUMENTS_IN_REVERSE_ORDER = Arrays
            .asList("-Xrunjdwp:transport=dt_socket,server=y,address=8000",
                    "-Xdebug");

    private static final List<String> POST_JAVA5_DEBUG_ARGUMENTS = Arrays
            .asList("-agentlib:jdwp=transport=dt_socket,server=y,address=8000");

    /**
     * Nasty rule that always fails
     */
    private static class FailOnExecution implements TestRule {

        public Statement apply(Statement base,
                Description description) {
            return new Statement() {

                @Override
                public void evaluate() throws Throwable {
                    throw new AssertionError();
                }
            };
        }

    }

    public static abstract class AbstractDisableOnDebugTest {

        @Rule
        public TestRule failOnExecution;

        public AbstractDisableOnDebugTest(List<String> arguments) {
            this.failOnExecution = new DisableOnDebug(new FailOnExecution(),
                    arguments);
        }

        @Test
        public void test() {
        }
    }

    public static class PreJava5DebugArgumentsTest extends
            AbstractDisableOnDebugTest {

        public PreJava5DebugArgumentsTest() {
            super(PRE_JAVA5_DEBUG_ARGUMENTS);
        }

    }

    public static class PreJava5DebugArgumentsReversedTest extends
            AbstractDisableOnDebugTest {

        public PreJava5DebugArgumentsReversedTest() {
            super(PRE_JAVA5_DEBUG_ARGUMENTS_IN_REVERSE_ORDER);
        }

    }

    public static class PostJava5DebugArgumentsTest extends
            AbstractDisableOnDebugTest {

        public PostJava5DebugArgumentsTest() {
            super(POST_JAVA5_DEBUG_ARGUMENTS);
        }

    }

    public static class WithoutDebugArgumentsTest extends
            AbstractDisableOnDebugTest {

        public WithoutDebugArgumentsTest() {
            super(WITHOUT_DEBUG_ARGUMENTS);
        }

    }

    @Test
    public void givenPreJava5DebugArgumentsIsDebuggingShouldReturnTrue() {
        DisableOnDebug subject = new DisableOnDebug(
                new FailOnExecution(), PRE_JAVA5_DEBUG_ARGUMENTS);
        assertTrue("Should be debugging", subject.isDebugging());
    }

    @Test
    public void givenPreJava5DebugArgumentsInReverseIsDebuggingShouldReturnTrue() {
        DisableOnDebug subject = new DisableOnDebug(
                new FailOnExecution(),
                PRE_JAVA5_DEBUG_ARGUMENTS_IN_REVERSE_ORDER);
        assertTrue("Should be debugging", subject.isDebugging());
    }

    @Test
    public void givenPostJava5DebugArgumentsIsDebuggingShouldReturnTrue() {
        DisableOnDebug subject = new DisableOnDebug(
                new FailOnExecution(), POST_JAVA5_DEBUG_ARGUMENTS);
        assertTrue("Should be debugging", subject.isDebugging());
    }

    @Test
    public void givenArgumentsWithoutDebugFlagsIsDebuggingShouldReturnFalse() {
        DisableOnDebug subject = new DisableOnDebug(
                new FailOnExecution(), WITHOUT_DEBUG_ARGUMENTS);
        Assert.assertFalse("Should not be debugging", subject.isDebugging());
    }

    @Test
    public void whenRunWithPreJava5DebugArgumentsTestShouldFail() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(PreJava5DebugArgumentsTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should not have failed", 0, result.getFailureCount());
    }

    @Test
    public void whenRunWithPreJava5DebugArgumentsInReverseOrderTestShouldFail() {
        JUnitCore core = new JUnitCore();
        Result result = core
                .run(PreJava5DebugArgumentsReversedTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should not have failed", 0, result.getFailureCount());
    }

    @Test
    public void whenRunWithPostJava5DebugArgumentsTestShouldFail() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(PostJava5DebugArgumentsTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should not have failed", 0, result.getFailureCount());
    }

    @Test
    public void whenRunWithoutDebugFlagsTestShouldPass() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(WithoutDebugArgumentsTest.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should have failed", 1, result.getFailureCount());
    }

}
