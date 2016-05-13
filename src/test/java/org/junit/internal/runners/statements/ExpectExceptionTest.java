package org.junit.internal.runners.statements;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;

import static org.junit.Assert.*;

public class ExpectExceptionTest {

    @Test
    public void givenWeAreExpectingAssumptionViolatedExceptionAndAStatementThrowingAssumptionViolatedException() {
        ExpectException sut = new ExpectException(new StatementThrowingAssumptionViolatedException(), AssumptionViolatedException.class);

        try {
            sut.evaluate();
            // then no exception should be thrown
        } catch (Throwable e) {
            fail("should not throw anything, but was thrown: " + e);
        }
    }


    private static class StatementThrowingAssumptionViolatedException extends Statement {
        @Override
        public void evaluate() throws Throwable {
            throw new AssumptionViolatedException("expected");
        }
    }
}