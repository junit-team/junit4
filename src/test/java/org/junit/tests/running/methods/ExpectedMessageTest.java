package org.junit.tests.running.methods;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Tests for the optional {@link org.junit.Test#expectedMessage()} parameter of the {@link Test} annotation.
 */
public class ExpectedMessageTest {

    public static class UnexpectedMessage {
        @Test(expectedMessage = "has to be greater than zero")
        public void unexpectedMessage() {
            throw new IllegalArgumentException("Parameter x cannot be null");
        }
    }

    @Test
    public void unexpectedMessage() {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(UnexpectedMessage.class);
        assertEquals(1, result.getFailureCount());
        assertEquals("Unexpected message, expected it to contain " +
                         "<has to be greater than zero> but was " +
                         "<Parameter x cannot be null>",
                     result.getFailures().get(0).getMessage());
    }

    public static class ExpectedExceptionTakesPrecedence {
        @Test(expected = IllegalArgumentException.class,
              expectedMessage = "Parameter x cannot be null")
        public void expectedExceptionTakesPrecedence() {
            throw new Error("Variable x cannot be null");
        }
    }

    @Test
    public void expectedExceptionTakesPrecedence() {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(ExpectedExceptionTakesPrecedence.class);
        assertEquals(1, result.getFailureCount());
        final Failure failure = result.getFailures().get(0);
        assertFalse(failure.getMessage().contains("Unexpected message"));
    }

    public static class NoneThrown {
        @Test(expected = IllegalArgumentException.class,
              expectedMessage = "x cannot be empty")
        public void noneThrown() {
            throw new IllegalArgumentException("Parameter x cannot be empty");
        }
    }

    @Test
    public void noneThrown() {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(NoneThrown.class);
        assertTrue(result.wasSuccessful());
    }

    public static class IndependentOfExpectedException {
        @Test(expectedMessage = "cannot be null")
        public void independentOfExpectedException() {
            throw new IllegalArgumentException("Variable cannot be null!");
        }
    }

    @Test
    public void independentOfExpectedException() {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(IndependentOfExpectedException.class);
        assertTrue(result.wasSuccessful());
    }
}
