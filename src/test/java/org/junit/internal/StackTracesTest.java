package org.junit.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import junit.tests.SampleJUnit3Tests;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.tests.SampleJUnit4Tests.*;

public class StackTracesTest {
    private static final String EOL = System.getProperty("line.separator", "\n");
    private static ExecutorService executorService;

    @BeforeClass
    public static void startExecutorService() {
        executorService = Executors.newFixedThreadPool(1);
    }

    @AfterClass
    public static void shutDownExecutorService() {
        executorService.shutdown();
        executorService = null;
    } 

    @Test
    public void getTrimmedStackForJUnit4TestFailingInTestMethod() {
        Result result = runTest(TestWithOneThrowingTestMethod.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: cause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.doThrowExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.throwsExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$TestWithOneThrowingTestMethod.alwaysThrows"));
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInTestMethodWithCause() {
        Result result = runTest(TestWithOneThrowingTestMethodWithCause.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: outer"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.doThrowExceptionWithCause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.throwsExceptionWithCause"),
                at("org.junit.tests.SampleJUnit4Tests$TestWithOneThrowingTestMethodWithCause.alwaysThrows"),
                framesTrimmed(),
                message("Caused by: java.lang.RuntimeException: cause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.doThrowExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.throwsExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.doThrowExceptionWithCause"),
                framesInCommon());
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInBeforeMethod() {
        Result result = runTest(TestWithThrowingBeforeMethod.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: cause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.doThrowExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.throwsExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$TestWithThrowingBeforeMethod.alwaysThrows"));
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit3TestFailingInTestMethod() {
        Result result = runTest(SampleJUnit3Tests.TestWithOneThrowingTestMethod.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: cause"),
                at("junit.tests.SampleJUnit3Tests$FakeClassUnderTest.doThrowExceptionWithoutCause"),
                at("junit.tests.SampleJUnit3Tests$FakeClassUnderTest.throwsExceptionWithoutCause"),
                at("junit.tests.SampleJUnit3Tests$TestWithOneThrowingTestMethod.testAlwaysThrows"));
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }
    
    @Test
    public void getTrimmedStackForJUnit3TestFailingInSetupMethod() {
        Result result = runTest(SampleJUnit3Tests.TestWithThrowingSetUpMethod.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: cause"),
                at("junit.tests.SampleJUnit3Tests$FakeClassUnderTest.doThrowExceptionWithoutCause"),
                at("junit.tests.SampleJUnit3Tests$FakeClassUnderTest.throwsExceptionWithoutCause"),
                at("junit.tests.SampleJUnit3Tests$TestWithThrowingSetUpMethod.setUp"));
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInTestRule() {
        Result result = runTest(TestWithThrowingTestRule.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: cause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.doThrowExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.throwsExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$ThrowingTestRule.apply"));
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInClassRule() {
        Result result = runTest(TestWithThrowingClassRule.class);
        assertEquals("No tests were executed", 0, result.getRunCount());
        assertEquals("One failure", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: cause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.doThrowExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.throwsExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$ThrowingTestRule.apply"));
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInMethodRule() {
        Result result = runTest(TestWithThrowingMethodRule.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: cause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.doThrowExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$FakeClassUnderTest.throwsExceptionWithoutCause"),
                at("org.junit.tests.SampleJUnit4Tests$ThrowingMethodRule.apply"));
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackWithSuppressedExceptions() {
        assumeTrue("Running on 1.7+", TestWithSuppressedException.addSuppressed != null);
        Result result = runTest(TestWithSuppressedException.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);

        assertHasTrimmedTrace(failure,
                message("java.lang.RuntimeException: error"),
                at("org.junit.tests.SampleJUnit4Tests$TestWithSuppressedException.alwaysThrows"),
                message("\tSuppressed: java.lang.RuntimeException: suppressed"),
                at("org.junit.tests.SampleJUnit4Tests$TestWithSuppressedException.alwaysThrows"),
                framesInCommon());
        assertNotEquals(failure.getTrace(), failure.getTrimmedTrace());
    }

    private abstract static class StringMatcher extends TypeSafeMatcher<String> {
    }

    /**
     * A matcher that matches the exception message in a stack trace.
     */
    private static class ExceptionMessageMatcher extends StringMatcher {
        private final Matcher<String> matcher;

        public ExceptionMessageMatcher(String message) {
            matcher = CoreMatchers.equalTo(message);
        }

        public void describeTo(Description description) {
            matcher.describeTo(description);
        }

        @Override
        protected boolean matchesSafely(String line) {
            return matcher.matches(line);
        }
    }

    /** Returns a matcher that matches the message line in a stack trace. */
    private static StringMatcher message(String message) {
        return new ExceptionMessageMatcher(message);
    }

    /**
     * A matcher that matches the "at ..." line in a stack trace.
     */
    private static class StackTraceLineMatcher extends StringMatcher {
        private static final Pattern PATTERN
                = Pattern.compile("\t*at ([a-zA-Z0-9.$]+)\\([a-zA-Z0-9]+\\.java:[0-9]+\\)");

        private final String method;

        public StackTraceLineMatcher(String method) {
            this.method = method;
        }

        public void describeTo(Description description) {
            description.appendText("A stack trace line for method " + method);
        }

        @Override
        protected boolean matchesSafely(String line) {
            if (!line.startsWith("\t")) {
                return false;
            }

            line = line.substring(1);
            java.util.regex.Matcher matcher = PATTERN.matcher(line);
            if (!matcher.matches()) {
                fail("Line does not look like a stack trace line: " + line);
            }
            String matchedMethod = matcher.group(1);
            return method.equals(matchedMethod);
        }
    }

    /** Returns a matcher that matches the "at ..." line in a stack trace. */
    private static StringMatcher at(String method) {
        return new StackTraceLineMatcher(method);
    }

    /**
     * A matcher that matches the line printed when frames were removed from a stack trace.
     */
    private static class FramesRemovedMatcher extends StringMatcher {
        private static final Pattern PATTERN
                = Pattern.compile("\t*\\.\\.\\. [0-9]+ ([a-z]+)");

        private final String suffix;

        public FramesRemovedMatcher(String suffix) {
            this.suffix = suffix;
        }

        public void describeTo(Description description) {
            description.appendText("A line matching \"..x " + suffix + "\"");
        }

        @Override
        protected boolean matchesSafely(String line) {
            if (!line.startsWith("\t")) {
                return false;
            }
            line = line.substring(1);

            java.util.regex.Matcher matcher = PATTERN.matcher(line);
            if (!matcher.matches()) {
                fail("Line does not look like a stack trace line: " + line);
            }
            return suffix.equals(matcher.group(1));
        }
    }

    /** Returns a matcher that matches the "\t...x more" line in a stack trace. */
    private static StringMatcher framesInCommon() {
        return new FramesRemovedMatcher("more");
    }

    /** Returns a matcher that matches the "\t...x trimmed" line in a stack trace. */
    private static StringMatcher framesTrimmed() {
        return new FramesRemovedMatcher("trimmed");
    }

    private static Result runTest(final Class<?> testClass) {
        Future<Result> future = executorService.submit(new Callable<Result>() {
            public Result call() throws Exception {
                JUnitCore core = new JUnitCore();
                return core.run(testClass);
            }
        });

        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not run test " + testClass, e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Could not run test " + testClass, e);
        }
    }
    
    private static void assertHasTrimmedTrace(Failure failure, StringMatcher... matchers) {
        String trimmedTrace = failure.getTrimmedTrace();
        String[] lines = trimmedTrace.split(EOL);

        int index = 0;
        for (; index < lines.length && index < matchers.length; index++) {
            String line = lines[index];
            StringMatcher matcher = matchers[index];
            assertThat(line, matcher);
        }
        if (index < lines.length) {
            String extraLine = lines[index];
            fail("Extra line in trimmed trace: " + extraLine);
        } else if (index < matchers.length) {
            StringDescription description = new StringDescription();
            matchers[index].describeTo(description);
            fail("Missing line in trimmed trace: " + description.toString());
        }
    }
}
