package org.junit.tests.internal.runners.statements;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Math.atan;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.internal.runners.statements.FailOnTimeout.builder;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestTimedOutException;

/**
 * @author Asaf Ary, Stefan Birkner
 */
public class FailOnTimeoutTest {
    private static final long TIMEOUT = 100;
    private static final long DURATION_THAT_EXCEEDS_TIMEOUT = 60 * 60 * 1000; //1 hour

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final TestStatement statement = new TestStatement();

    private final FailOnTimeout failOnTimeout = builder().withTimeout(TIMEOUT, MILLISECONDS).build(statement);

    @Test
    public void throwsTestTimedOutException() throws Throwable {
        thrown.expect(TestTimedOutException.class);
        evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT);
    }

    @Test
    public void throwExceptionWithNiceMessageOnTimeout() throws Throwable {
        thrown.expectMessage("test timed out after 100 milliseconds");
        evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT);
    }

    @Test
    public void sendUpExceptionThrownByStatement() throws Throwable {
        RuntimeException exception = new RuntimeException();
        thrown.expect(is(exception));
        evaluateWithException(exception);
    }

    @Test
    public void throwExceptionIfTheSecondCallToEvaluateNeedsTooMuchTime()
            throws Throwable {
        thrown.expect(TestTimedOutException.class);
        evaluateWithWaitDuration(0);
        evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT);
    }

    @Test
    public void throwTimeoutExceptionOnSecondCallAlthoughFirstCallThrowsException()
            throws Throwable {
        thrown.expectMessage("test timed out after 100 milliseconds");
        try {
            evaluateWithException(new RuntimeException());
        } catch (Throwable expected) {
        }
        evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT);
    }

    @Test
    public void throwsExceptionWithTimeoutValueAndTimeUnitSet()
            throws Throwable {
        try {
            evaluateWithWaitDuration(DURATION_THAT_EXCEEDS_TIMEOUT);
            fail("No exception was thrown when test timed out");
        } catch (TestTimedOutException e) {
            assertEquals(TIMEOUT, e.getTimeout());
            assertEquals(TimeUnit.MILLISECONDS, e.getTimeUnit());
        }
    }

    private void evaluateWithException(Exception exception) throws Throwable {
        statement.nextException = exception;
        statement.waitDuration = 0;
        failOnTimeout.evaluate();
    }

    private void evaluateWithWaitDuration(long waitDuration) throws Throwable {
        statement.nextException = null;
        statement.waitDuration = waitDuration;
        failOnTimeout.evaluate();
    }

    private static final class TestStatement extends Statement {
        long waitDuration;

        Exception nextException;

        @Override
        public void evaluate() throws Throwable {
            sleep(waitDuration);
            if (nextException != null) {
                throw nextException;
            }
        }
    }

    @Test
    public void stopEndlessStatement() throws Throwable {
        InfiniteLoopStatement infiniteLoop = new InfiniteLoopStatement();
        FailOnTimeout infiniteLoopTimeout = builder().withTimeout(TIMEOUT, MILLISECONDS).build(infiniteLoop);
        try {
            infiniteLoopTimeout.evaluate();
        } catch (Exception timeoutException) {
            sleep(20); // time to interrupt the thread
            int firstCount = InfiniteLoopStatement.COUNT;
            sleep(20); // time to increment the count
            assertTrue("Thread has not been stopped.",
                    firstCount == InfiniteLoopStatement.COUNT);
        }
    }

    private static final class InfiniteLoopStatement extends Statement {
        private static int COUNT = 0;

        @Override
        public void evaluate() throws Throwable {
            while (true) {
                sleep(10); // sleep in order to enable interrupting thread
                ++COUNT;
            }
        }
    }

    @Test
    public void stackTraceContainsRealCauseOfTimeout() throws Throwable {
        StuckStatement stuck = new StuckStatement();
        FailOnTimeout stuckTimeout = builder().withTimeout(TIMEOUT, MILLISECONDS).build(stuck);
        try {
            stuckTimeout.evaluate();
            // We must not get here, we expect a timeout exception
            fail("Expected timeout exception");
        } catch (Exception timeoutException) {
            StackTraceElement[] stackTrace = timeoutException.getStackTrace();
            boolean stackTraceContainsTheRealCauseOfTheTimeout = false;
            boolean stackTraceContainsOtherThanTheRealCauseOfTheTimeout = false;
            for (StackTraceElement element : stackTrace) {
                String methodName = element.getMethodName();
                if ("theRealCauseOfTheTimeout".equals(methodName)) {
                    stackTraceContainsTheRealCauseOfTheTimeout = true;
                }
                if ("notTheRealCauseOfTheTimeout".equals(methodName)) {
                    stackTraceContainsOtherThanTheRealCauseOfTheTimeout = true;
                }
            }
            assertTrue(
                    "Stack trace does not contain the real cause of the timeout",
                    stackTraceContainsTheRealCauseOfTheTimeout);
            assertFalse(
                    "Stack trace contains other than the real cause of the timeout, which can be very misleading",
                    stackTraceContainsOtherThanTheRealCauseOfTheTimeout);
        }
    }

    private static final class StuckStatement extends Statement {

        @Override
        public void evaluate() throws Throwable {
            try {
                // Must show up in stack trace
                theRealCauseOfTheTimeout();
            } catch (InterruptedException e) {
            } finally {
                // Must _not_ show up in stack trace
                notTheRealCauseOfTheTimeout();
            }
        }

        private void theRealCauseOfTheTimeout() throws InterruptedException {
            sleep(MAX_VALUE);
        }

        private void notTheRealCauseOfTheTimeout() {
            for (long now = currentTimeMillis(), eta = now + 1000L; now < eta; now = currentTimeMillis()) {
                // Doesn't matter, just pretend to be busy
                atan(now);
            }
        }
    }
}
