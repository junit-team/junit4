package org.junit.rules;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.ThrowableCauseMatcher.hasCause;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;

/**
 * The {@code ExpectedException} rule allows you to verify that your code
 * throws a specific exception.
 *
 * <h3>Usage</h3>
 *
 * <pre> public class SimpleExpectedExceptionTest {
 *     &#064;Rule
 *     public ExpectedException thrown= ExpectedException.none();
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
 * <p>
 * You have to add the {@code ExpectedException} rule to your test.
 * This doesn't affect your existing tests (see {@code throwsNothing()}).
 * After specifiying the type of the expected exception your test is
 * successful when such an exception is thrown and it fails if a
 * different or no exception is thrown.
 *
 * <p>
 * Instead of specifying the exception's type you can characterize the
 * expected exception based on other criterias, too:
 *
 * <ul>
 *   <li>The exception's message contains a specific text: {@link #expectMessage(String)}</li>
 *   <li>The exception's message complies with a Hamcrest matcher: {@link #expectMessage(Matcher)}</li>
 *   <li>The exception's cause complies with a Hamcrest matcher: {@link #expectCause(Matcher)}</li>
 *   <li>The exception itself complies with a Hamcrest matcher: {@link #expect(Matcher)}</li>
 * </ul>
 *
 * <p>
 * You can combine any of the presented expect-methods. The test is
 * successful if all specifications are met.
 * <pre> &#064;Test
 * public void throwsException() {
 *     thrown.expect(NullPointerException.class);
 *     thrown.expectMessage(&quot;happened&quot;);
 *     throw new NullPointerException(&quot;What happened?&quot;);
 * }</pre>
 *
 * <h3>Verify AssertionErrors and AssumptionViolatedExceptions</h3>
 *
 * <p>
 * By default ExpectedException rule doesn't handle AssertionErrors and
 * AssumptionViolatedExceptions, because such exceptions are used by JUnit. If
 * you want to handle such exceptions you have to call @link
 * {@link #handleAssertionErrors()} or @link
 * {@link #handleAssumptionViolatedExceptions()}.
 *
 * <pre>
 * // These tests all pass.
 * public static class HasExpectedException {
 * 	&#064;Rule
 * 	public ExpectedException thrown= ExpectedException.none();
 *
 * 	&#064;Test
 * 	public void throwExpectedAssertionError() {
 * 		thrown.handleAssertionErrors();
 * 		thrown.expect(AssertionError.class);
 * 		throw new AssertionError();
 *     }
 *
 *  &#064;Test
 *  public void throwExpectAssumptionViolatedException() {
 *      thrown.handleAssumptionViolatedExceptions();
 *      thrown.expect(AssumptionViolatedException.class);
 *      throw new AssumptionViolatedException(&quot;&quot;);
 *     }
 * }
 * </pre>
 *
 * <h3>Missing Exceptions</h3>
 * <p>
 * By default missing exceptions are reported with an error message
 * like "Expected test to throw foo.". You can configure a different
 * message by means of {@link #reportMissingExceptionWithMessage(String)}.
 *
 * @since 4.7
 */
public class ExpectedException implements TestRule {
    /**
     * Returns a {@linkplain TestRule rule} that expects no exception to
     * be thrown (identical to behavior without this rule).
     */
    public static ExpectedException none() {
        return new ExpectedException();
    }

    private final ExpectedExceptionMatcherBuilder fMatcherBuilder = new ExpectedExceptionMatcherBuilder();

    private boolean handleAssumptionViolatedExceptions = false;

    private boolean handleAssertionErrors = false;
    
    private String missingExceptionMessage;

    private ExpectedException() {
    }

    /**
     * {@code AssertionErrors} are only considered by the rule if you call
     * this method.
     * @return the rule itself
     */
    public ExpectedException handleAssertionErrors() {
        handleAssertionErrors = true;
        return this;
    }

    /**
     * {@code AssumptionViolatedExceptions} are only considered by the rule
     * if you call this method.
     * @return the rule itself
     */
    public ExpectedException handleAssumptionViolatedExceptions() {
        handleAssumptionViolatedExceptions = true;
        return this;
    }
    
    /**
     * Specifies the failure message for tests that are expected to throw 
     * an exception but do not throw any.
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
        fMatcherBuilder.add(matcher);
    }

    /**
     * Verify that your code throws an exception that is an
     * instance of specific {@code type}.
     * <pre> &#064;Test
     * public void throwsExceptionWithSpecificType() {
     *     thrown.expect(NullPointerException.class);
     *     throw new NullPointerException();
     * }
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
    public void expectCause(Matcher<? extends Throwable> expectedCause) {
        expect(hasCause(expectedCause));
    }

    private class ExpectedExceptionStatement extends Statement {
        private final Statement fNext;

        public ExpectedExceptionStatement(Statement base) {
            fNext = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                fNext.evaluate();
            } catch (AssumptionViolatedException e) {
                optionallyHandleException(e, handleAssumptionViolatedExceptions);
                return;
            } catch (AssertionError e) {
                optionallyHandleException(e, handleAssertionErrors);
                return;
            } catch (Throwable e) {
                handleException(e);
                return;
            }
            if (fMatcherBuilder.expectsThrowable()) {
                failDueToMissingException();
            }
        }
    }

    private void optionallyHandleException(Throwable e, boolean handleException)
            throws Throwable {
        if (handleException) {
            handleException(e);
        } else {
            throw e;
        }
    }

    private void handleException(Throwable e) throws Throwable {
        if (fMatcherBuilder.expectsThrowable()) {
            assertThat(e, fMatcherBuilder.build());
        } else {
            throw e;
        }
    }

    private void failDueToMissingException() throws AssertionError {
        fail(missingExceptionMessage());
    }
    
    private String missingExceptionMessage() {
        if (isMissingExceptionMessageEmpty()) {
            String expectation = StringDescription.toString(fMatcherBuilder.build());
            return "Expected test to throw " + expectation;
        } else {
            return missingExceptionMessage;
        }        
    }
    
    private boolean isMissingExceptionMessageEmpty() {
        return missingExceptionMessage == null || missingExceptionMessage.length() == 0;
    }
}
