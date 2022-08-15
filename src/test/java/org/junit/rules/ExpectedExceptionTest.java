package org.junit.rules;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.junit.rules.ExpectedException.none;
import static org.junit.rules.EventCollector.everyTestRunSuccessful;
import static org.junit.rules.EventCollector.hasSingleAssumptionFailure;
import static org.junit.rules.EventCollector.hasSingleFailure;
import static org.junit.rules.EventCollector.hasSingleFailureWithMessage;

import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ExpectedExceptionTest {
    private static final String ARBITRARY_MESSAGE = "arbitrary message";

    @Parameters(name= "{0}")
    public static Collection<Object[]> testsWithEventMatcher() {
        return asList(new Object[][]{
                {EmptyTestExpectingNoException.class, everyTestRunSuccessful()},
                {ThrowExceptionWithExpectedType.class,
                        everyTestRunSuccessful()},
                {ThrowExceptionWithExpectedPartOfMessage.class,
                        everyTestRunSuccessful()},
                {
                        ThrowExceptionWithWrongType.class,
                        hasSingleFailureWithMessage(startsWith("\nExpected: an instance of java.lang.NullPointerException"))},
                {
                        HasWrongMessage.class,
                        hasSingleFailureWithMessage(startsWith("\nExpected: exception with message a string containing \"expectedMessage\"\n"
                                + "     but: message was \"actualMessage\""))},
                {
                        ThrowNoExceptionButExpectExceptionWithType.class,
                        hasSingleFailureWithMessage("Expected test to throw an instance of java.lang.NullPointerException")},
                {WronglyExpectsExceptionMessage.class, hasSingleFailure()},
                {ExpectsSubstring.class, everyTestRunSuccessful()},
                {
                        ExpectsSubstringNullMessage.class,
                        hasSingleFailureWithMessage(startsWith("\nExpected: exception with message a string containing \"anything!\""))},
                {ExpectsMessageMatcher.class, everyTestRunSuccessful()},
                {
                        ExpectedMessageMatcherFails.class,
                        hasSingleFailureWithMessage(startsWith("\nExpected: exception with message \"Wrong start\""))},
                {ExpectsMatcher.class, everyTestRunSuccessful()},
                {ExpectAssertionErrorWhichIsNotThrown.class, hasSingleFailure()},
                {FailedAssumptionAndExpectException.class,
                        hasSingleAssumptionFailure()},
                {FailBeforeExpectingException.class,
                        hasSingleFailureWithMessage(ARBITRARY_MESSAGE)},
                {
                        ExpectsMultipleMatchers.class,
                        hasSingleFailureWithMessage(startsWith("\nExpected: (an instance of java.lang.IllegalArgumentException and exception with message a string containing \"Ack!\")"))},
                {ThrowExceptionWithMatchingCause.class, everyTestRunSuccessful()},
                {ThrowExpectedNullCause.class, everyTestRunSuccessful()},
                {
                        ThrowUnexpectedCause.class,
                        hasSingleFailureWithMessage(CoreMatchers.<String>allOf(
                                startsWith("\nExpected: ("),
                                containsString("exception with cause is <java.lang.NullPointerException: expected cause>"),
                                containsString("cause was <java.lang.NullPointerException: an unexpected cause>"),
                                containsString("Stacktrace was: java.lang.IllegalArgumentException: Ack!"),
                                containsString("Caused by: java.lang.NullPointerException: an unexpected cause")))},
                {
                        UseNoCustomMessage.class,
                        hasSingleFailureWithMessage("Expected test to throw an instance of java.lang.IllegalArgumentException") },
                {
                        UseCustomMessageWithoutPlaceHolder.class,
                        hasSingleFailureWithMessage(ARBITRARY_MESSAGE) },
                {
                        UseCustomMessageWithPlaceHolder.class,
                        hasSingleFailureWithMessage(ARBITRARY_MESSAGE
                                + " - an instance of java.lang.IllegalArgumentException") },
                {
                        ErrorCollectorShouldFailAlthoughExpectedExceptionDoesNot.class,
                        hasSingleFailureWithMessage(ARBITRARY_MESSAGE) }
        });
    }

    private final Class<?> classUnderTest;

    private final Matcher<EventCollector> matcher;

    public ExpectedExceptionTest(Class<?> classUnderTest,
            Matcher<EventCollector> matcher) {
        this.classUnderTest = classUnderTest;
        this.matcher = matcher;
    }

    @Test
    public void runTestAndVerifyResult() {
        EventCollector collector = new EventCollector();
        JUnitCore core = new JUnitCore();
        core.addListener(collector);
        core.run(classUnderTest);
        assertThat(collector, matcher);
    }

    public static class EmptyTestExpectingNoException {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsNothing() {
        }
    }

    public static class ThrowExceptionWithExpectedType {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsNullPointerException() {
            thrown.expect(NullPointerException.class);
            throw new NullPointerException();
        }
    }

    public static class ThrowExceptionWithExpectedPartOfMessage {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsNullPointerExceptionWithMessage() {
            thrown.expect(NullPointerException.class);
            thrown.expectMessage(ARBITRARY_MESSAGE);
            throw new NullPointerException(ARBITRARY_MESSAGE + "something else");
        }
    }

    public static class ThrowExceptionWithWrongType {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsNullPointerException() {
            thrown.expect(NullPointerException.class);
            throw new IllegalArgumentException();
        }
    }

    public static class HasWrongMessage {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsNullPointerException() {
            thrown.expectMessage("expectedMessage");
            throw new IllegalArgumentException("actualMessage");
        }
    }

    public static class ThrowNoExceptionButExpectExceptionWithType {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void doesntThrowNullPointerException() {
            thrown.expect(NullPointerException.class);
        }
    }

    public static class WronglyExpectsExceptionMessage {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void doesntThrowAnything() {
            thrown.expectMessage("anything!");
        }
    }

    public static class ExpectsSubstring {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsMore() {
            thrown.expectMessage("anything!");
            throw new NullPointerException(
                    "This could throw anything! (as long as it has the right substring)");
        }
    }

    public static class ExpectsSubstringNullMessage {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsMore() {
            thrown.expectMessage("anything!");
            throw new NullPointerException();
        }
    }

    public static class ExpectsMessageMatcher {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsMore() {
            thrown.expectMessage(startsWith(ARBITRARY_MESSAGE));
            throw new NullPointerException(ARBITRARY_MESSAGE + "!");
        }
    }

    public static class ExpectedMessageMatcherFails {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsMore() {
            thrown.expectMessage(equalTo("Wrong start"));
            throw new NullPointerException("Back!");
        }
    }

    public static class ExpectsMatcher {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsMore() {
            thrown.expect(any(Exception.class));
            throw new NullPointerException("Ack!");
        }
    }

    public static class ExpectsMultipleMatchers {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwsMore() {
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Ack!");
            throw new NullPointerException("Ack!");
        }
    }

    //https://github.com/junit-team/junit4/pull/583
    public static class ExpectAssertionErrorWhichIsNotThrown {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void fails() {
            thrown.expect(AssertionError.class);
        }
    }

    public static class FailBeforeExpectingException {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void fails() {
            fail(ARBITRARY_MESSAGE);
            thrown.expect(IllegalArgumentException.class);
        }
    }

    public static class FailedAssumptionAndExpectException {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void failedAssumption() {
            assumeTrue(false);
            thrown.expect(NullPointerException.class);
        }
    }

    public static class ThrowExceptionWithMatchingCause {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwExceptionWithMatchingCause() {
            NullPointerException expectedCause = new NullPointerException("expected cause");

            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Ack!");
            thrown.expectCause(is(expectedCause));

            throw new IllegalArgumentException("Ack!", expectedCause);
        }
    }

    public static class ThrowExpectedNullCause {
        @Rule
        public ExpectedException thrown = none();

        @Test
        public void throwExpectedNullCause() {
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Ack!");
            thrown.expectCause(nullValue(Throwable.class));

            throw new IllegalArgumentException("Ack!");
        }
    }

    public static class ThrowUnexpectedCause {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwWithCause() {
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Ack!");
            thrown.expectCause(is(new NullPointerException("expected cause")));

            throw new IllegalArgumentException("Ack!", new NullPointerException("an unexpected cause"));
        }
    }
    
    public static class UseNoCustomMessage {

        @Rule
        public ExpectedException thrown= ExpectedException.none();

        @Test
        public void noThrow() {
            thrown.expect(IllegalArgumentException.class);
        }
    }

    public static class UseCustomMessageWithPlaceHolder {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void noThrow() {
            thrown.expect(IllegalArgumentException.class);
            thrown.reportMissingExceptionWithMessage(ARBITRARY_MESSAGE
                    + " - %s");
        }
    }

    public static class UseCustomMessageWithoutPlaceHolder {

        @Rule
        public ExpectedException thrown= ExpectedException.none();

        @Test
        public void noThrow() {
            thrown.expect(IllegalArgumentException.class);
            thrown.reportMissingExceptionWithMessage(ARBITRARY_MESSAGE);
        }
    }

    public static class ErrorCollectorShouldFailAlthoughExpectedExceptionDoesNot {

        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Rule(order = Integer.MAX_VALUE)
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void test() {
            collector.addError(new AssertionError(ARBITRARY_MESSAGE));
            thrown.expect(Exception.class);
            throw new RuntimeException();
        }
    }
}