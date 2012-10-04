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
 * The ExpectedException rule allows in-test specification of expected exception
 * types and messages:
 *
 * <pre>
 * // These tests all pass.
 * public static class HasExpectedException {
 * 	&#064;Rule
 * 	public ExpectedException thrown= ExpectedException.none();
 *
 * 	&#064;Test
 * 	public void throwsNothing() {
 * 		// no exception expected, none thrown: passes.
 *     }
 *
 * 	&#064;Test
 * 	public void throwsNullPointerException() {
 * 		thrown.expect(NullPointerException.class);
 * 		throw new NullPointerException();
 *     }
 *
 * 	&#064;Test
 * 	public void throwsNullPointerExceptionWithMessage() {
 * 		thrown.expect(NullPointerException.class);
 * 		thrown.expectMessage(&quot;happened?&quot;);
 * 		thrown.expectMessage(startsWith(&quot;What&quot;));
 * 		throw new NullPointerException(&quot;What happened?&quot;);
 *     }
 *
 * 	&#064;Test
 * 	public void throwsIllegalArgumentExceptionWithMessageAndCause() {
 * 		NullPointerException expectedCause = new NullPointerException();
 * 		thrown.expect(IllegalArgumentException.class);
 * 		thrown.expectMessage(&quot;What&quot;);
 * 		thrown.expectCause(is(expectedCause));
 * 		throw new IllegalArgumentException(&quot;What happened?&quot;, cause);
 *     }
 * }
 * </pre>
 *
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
 * @since 4.7
 */
public class ExpectedException implements TestRule {
    /**
     * @return a Rule that expects no exception to be thrown (identical to
     *         behavior without this Rule)
     */
    public static ExpectedException none() {
        return new ExpectedException();
    }

    private final ExpectedExceptionMatcherBuilder fMatcherBuilder = new ExpectedExceptionMatcherBuilder();

    private boolean handleAssumptionViolatedExceptions = false;

    private boolean handleAssertionErrors = false;

    private ExpectedException() {
    }

    public ExpectedException handleAssertionErrors() {
        handleAssertionErrors = true;
        return this;
    }

    public ExpectedException handleAssumptionViolatedExceptions() {
        handleAssumptionViolatedExceptions = true;
        return this;
    }

    public Statement apply(Statement base,
            org.junit.runner.Description description) {
        return new ExpectedExceptionStatement(base);
    }

    /**
     * Adds {@code matcher} to the list of requirements for any thrown
     * exception.
     */
    public void expect(Matcher<?> matcher) {
        fMatcherBuilder.add(matcher);
    }

    /**
     * Adds to the list of requirements for any thrown exception that it should
     * be an instance of {@code type}
     */
    public void expect(Class<? extends Throwable> type) {
        expect(instanceOf(type));
    }

    /**
     * Adds to the list of requirements for any thrown exception that it should
     * <em>contain</em> string {@code substring}
     */
    public void expectMessage(String substring) {
        expectMessage(containsString(substring));
    }

    /**
     * Adds {@code matcher} to the list of requirements for the message returned
     * from any thrown exception.
     */
    public void expectMessage(Matcher<String> matcher) {
        expect(hasMessage(matcher));
    }

    /**
     * Adds {@code matcher} to the list of requirements for the cause of
     * any thrown exception.
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
                if (fMatcherBuilder.expectsThrowable()) {
                    failDueToMissingException();
                }
            } catch (AssumptionViolatedException e) {
                optionallyHandleException(e, handleAssumptionViolatedExceptions);
            } catch (AssertionError e) {
                optionallyHandleException(e, handleAssertionErrors);
            } catch (Throwable e) {
                handleException(e);
            }
        }
    }

    private void failDueToMissingException() throws AssertionError {
        String expectation = StringDescription.toString(fMatcherBuilder.build());
        fail("Expected test to throw " + expectation);
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
}
