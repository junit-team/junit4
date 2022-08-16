package org.junit.rules;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.ThrowableCauseMatcher.hasCause;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;
import org.junit.AssumptionViolatedException;
import org.junit.runners.model.Statement;

/**
 * The {@code ExpectedException} rule allows you to verify that your code
 * throws a specific exception.
 *
 * <h3>Usage</h3>
 *
 * <pre> public class SimpleExpectedExceptionTest {
 *     &#064;Rule
 *     public ExpectedException thrown = ExpectedException.none();
 *
 *     &#064;Test
 *     public void throwsNothing() {
 *         // no exception expected, none thrown: passes.
 *     }
 *
 *     &#064;Test
 *     public void throwsExceptionWithSpecificType() {
 *         thrown.expect(NullPointerException.class);
 *         throw new NullPointerException();
 *     }
 * }</pre>
 * 
 * <p>You have to add the {@code ExpectedException} rule to your test.
 * This doesn't affect your existing tests (see {@code throwsNothing()}).
 * After specifying the type of the expected exception your test is
 * successful when such an exception is thrown and it fails if a
 * different or no exception is thrown.
 *
 * <p>This rule does not perform any special magic to make execution continue
 * as if the exception had not been thrown. So it is nearly always a mistake
 * for a test method to have statements after the one that is expected to
 * throw the exception.
 *
 * <p>Instead of specifying the exception's type you can characterize the
 * expected exception based on other criteria, too:
 *
 * <ul>
 *   <li>The exception's message contains a specific text: {@link #expectMessage(String)}</li>
 *   <li>The exception's message complies with a Hamcrest matcher: {@link #expectMessage(Matcher)}</li>
 *   <li>The exception's cause complies with a Hamcrest matcher: {@link #expectCause(Matcher)}</li>
 *   <li>The exception itself complies with a Hamcrest matcher: {@link #expect(Matcher)}</li>
 * </ul>
 *
 * <p>You can combine any of the presented expect-methods. The test is
 * successful if all specifications are met.
 * <pre> &#064;Test
 * public void throwsException() {
 *     thrown.expect(NullPointerException.class);
 *     thrown.expectMessage(&quot;happened&quot;);
 *     throw new NullPointerException(&quot;What happened?&quot;);
 * }</pre>
 *
 * <p>It is recommended to set the {@link org.junit.Rule#order() order} of the
 * {@code ExpectedException} to {@code Integer.MAX_VALUE} if it is used together
 * with another rule that handles exceptions, e.g. {@link ErrorCollector}.
 * Otherwise failing tests may be successful.
 * <pre> &#064;Rule(order = Integer.MAX_VALUE)
 * public ExpectedException thrown = ExpectedException.none();</pre>
 *
 * <h3>AssumptionViolatedExceptions</h3>
 * <p>JUnit uses {@link AssumptionViolatedException}s for indicating that a test
 * provides no useful information. (See {@link org.junit.Assume} for more
 * information.) You have to call {@code assume} methods before you set
 * expectations of the {@code ExpectedException} rule. In this case the rule
 * will not handle consume the exceptions and it can be handled by the
 * framework. E.g. the following test is ignored by JUnit's default runner.
 *
 * <pre> &#064;Test
 * public void ignoredBecauseOfFailedAssumption() {
 *     assumeTrue(false); // throws AssumptionViolatedException
 *     thrown.expect(NullPointerException.class);
 * }</pre>
 *
 * <h3>AssertionErrors</h3>
 *
 * <p>JUnit uses {@link AssertionError}s for indicating that a test is failing. You
 * have to call {@code assert} methods before you set expectations of the
 * {@code ExpectedException} rule, if they should be handled by the framework.
 * E.g. the following test fails because of the {@code assertTrue} statement.
 *
 * <pre> &#064;Test
 * public void throwsUnhandled() {
 *     assertTrue(false); // throws AssertionError
 *     thrown.expect(NullPointerException.class);
 * }</pre>
 *
 * <h3>Missing Exceptions</h3>
 * <p>By default missing exceptions are reported with an error message
 * like "Expected test to throw an instance of foo". You can configure a different
 * message by means of {@link #reportMissingExceptionWithMessage(String)}. You
 * can use a {@code %s} placeholder for the description of the expected
 * exception. E.g. "Test doesn't throw %s." will fail with the error message
 * "Test doesn't throw an instance of foo.".
 *
 * @since 4.7
 */
public class ExpectedException implements TestRule {
    /**
     * Returns a {@linkplain TestRule rule} that expects no exception to
     * be thrown (identical to behavior without this rule).
     *
     * @deprecated Since 4.13
     * {@link org.junit.Assert#assertThrows(Class, org.junit.function.ThrowingRunnable)
     * Assert.assertThrows} can be used to verify that your code throws a specific
     * exception.
     */
    @Deprecated
    public static ExpectedException none() {
        return new ExpectedException();
    }

    private final ExpectedExceptionMatcherBuilder matcherBuilder = new ExpectedExceptionMatcherBuilder();

    private String missingExceptionMessage= "Expected test to throw %s";

    private ExpectedException() {
    }

    /**
     * This method does nothing. Don't use it.
     * @deprecated AssertionErrors are handled by default since JUnit 4.12. Just
     *             like in JUnit &lt;= 4.10.
     */
    @Deprecated
    public ExpectedException handleAssertionErrors() {
        return this;
    }

    /**
     * This method does nothing. Don't use it.
     * @deprecated AssumptionViolatedExceptions are handled by default since
     *             JUnit 4.12. Just like in JUnit &lt;= 4.10.
     */
    @Deprecated
    public ExpectedException handleAssumptionViolatedExceptions() {
        return this;
    }

    /**
     * Specifies the failure message for tests that are expected to throw 
     * an exception but do not throw any. You can use a {@code %s} placeholder for
     * the description of the expected exception. E.g. "Test doesn't throw %s."
     * will fail with the error message
     * "Test doesn't throw an instance of foo.".
     *
     * @param message exception detail message
     * @return the rule itself
     */
    public ExpectedException reportMissingExceptionWithMessage(String message) {
        missingExceptionMessage = message;
        return this;
    }

    public Statement apply(Statement base,
            org.junit.runner.Description description) {
        return new ExpectedExceptionStatement(base);
    }

    /**
     * Verify that your code throws an exception that is matched by
     * a Hamcrest matcher.
     * <pre> &#064;Test
     * public void throwsExceptionThatCompliesWithMatcher() {
     *     NullPointerException e = new NullPointerException();
     *     thrown.expect(is(e));
     *     throw e;
     * }</pre>
     */
    public void expect(Matcher<?> matcher) {
        matcherBuilder.add(matcher);
    }

    /**
     * Verify that your code throws an exception that is an
     * instance of specific {@code type}.
     * <pre> &#064;Test
     * public void throwsExceptionWithSpecificType() {
     *     thrown.expect(NullPointerException.class);
     *     throw new NullPointerException();
     * }</pre>
     */
    public void expect(Class<? extends Throwable> type) {
        expect(instanceOf(type));
    }

    /**
     * Verify that your code throws an exception whose message contains
     * a specific text.
     * <pre> &#064;Test
     * public void throwsExceptionWhoseMessageContainsSpecificText() {
     *     thrown.expectMessage(&quot;happened&quot;);
     *     throw new NullPointerException(&quot;What happened?&quot;);
     * }</pre>
     */
    public void expectMessage(String substring) {
        expectMessage(containsString(substring));
    }

    /**
     * Verify that your code throws an exception whose message is matched 
     * by a Hamcrest matcher.
     * <pre> &#064;Test
     * public void throwsExceptionWhoseMessageCompliesWithMatcher() {
     *     thrown.expectMessage(startsWith(&quot;What&quot;));
     *     throw new NullPointerException(&quot;What happened?&quot;);
     * }</pre>
     */
    public void expectMessage(Matcher<String> matcher) {
        expect(hasMessage(matcher));
    }

    /**
     * Verify that your code throws an exception whose cause is matched by 
     * a Hamcrest matcher.
     * <pre> &#064;Test
     * public void throwsExceptionWhoseCauseCompliesWithMatcher() {
     *     NullPointerException expectedCause = new NullPointerException();
     *     thrown.expectCause(is(expectedCause));
     *     throw new IllegalArgumentException(&quot;What happened?&quot;, cause);
     * }</pre>
     */
    public void expectCause(Matcher<?> expectedCause) {
        expect(hasCause(expectedCause));
    }

    /**
     * Check if any Exception is expected.
     * @since 4.13
     */
    public final boolean isAnyExceptionExpected() {
        return matcherBuilder.expectsThrowable();
    }

    private class ExpectedExceptionStatement extends Statement {
        private final Statement next;

        public ExpectedExceptionStatement(Statement base) {
            next = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                next.evaluate();
            } catch (Throwable e) {
                handleException(e);
                return;
            }
            if (isAnyExceptionExpected()) {
                failDueToMissingException();
            }
        }
    }

    private void handleException(Throwable e) throws Throwable {
        if (isAnyExceptionExpected()) {
            MatcherAssert.assertThat(e, matcherBuilder.build());
        } else {
            throw e;
        }
    }

    private void failDueToMissingException() throws AssertionError {
        fail(missingExceptionMessage());
    }
    
    private String missingExceptionMessage() {
        String expectation= StringDescription.toString(matcherBuilder.build());
        return format(missingExceptionMessage, expectation);
    }
}
