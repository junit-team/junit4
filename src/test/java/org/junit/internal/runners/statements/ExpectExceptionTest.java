package org.junit.internal.runners.statements;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Integration tests can be found in {@link org.junit.tests.running.methods.ExpectedTest}.
 * See e.g. {@link org.junit.tests.running.methods.ExpectedTest#expectsAssumptionViolatedException()}
 */
public class ExpectExceptionTest {

    @Test
    public void whenExpectingAssumptionViolatedExceptionStatementsThrowingItShouldPass() {
        Statement statementThrowingAssumptionViolatedException = new Fail(new AssumptionViolatedException("expected"));
        ExpectException expectException = new ExpectException(statementThrowingAssumptionViolatedException, AssumptionViolatedException.class);

        try {
            expectException.evaluate();
            // then no exception should be thrown
        } catch (Throwable e) {
            fail("should not throw anything, but was thrown: " + e);
        }
    }

    @Test
    public void whenExpectingAssumptionViolatedExceptionStatementsThrowingSubclassShouldPass() {
        Statement statementThrowingAssumptionViolatedExceptionSubclass = new Fail(new org.junit.AssumptionViolatedException("expected"));
        ExpectException expectException = new ExpectException(statementThrowingAssumptionViolatedExceptionSubclass, AssumptionViolatedException.class);

        try {
            expectException.evaluate();
            // then no exception should be thrown
        } catch (Throwable e) {
            fail("should not throw anything, but was thrown: " + e);
        }
    }

    @Test
    public void whenExpectingAssumptionViolatedExceptionStatementsThrowingDifferentExceptionShouldFail() {
        Statement statementThrowingSomeException = new Fail(new SomeException("not expected"));
        ExpectException expectException = new ExpectException(statementThrowingSomeException, AssumptionViolatedException.class);

        try {
            expectException.evaluate();
            fail("should throw 'Unexpected exception' when statement throws an exception which is not the one expected");
        } catch (Exception e) {
            assertThat(e.getMessage(), equalTo("Unexpected exception, expected<org.junit.internal.AssumptionViolatedException> " +
                    "but was<org.junit.internal.runners.statements.ExpectExceptionTest$SomeException>"));
        }
    }

    @Test
    public void whenExpectingAssumptionViolatedExceptionStatementsPassingShouldFail() throws Exception {
        ExpectException expectException = new ExpectException(new PassingStatement(), AssumptionViolatedException.class);

        try {
            expectException.evaluate();
            fail("ExpectException should throw when the given statement passes");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("Expected exception: " + AssumptionViolatedException.class.getName()));
        }
    }

    private static class PassingStatement extends Statement {
        public void evaluate() throws Throwable {
            // nop
        }
    }

    private static class SomeException extends RuntimeException {
        public SomeException(String message) {
            super(message);
        }
    }
}