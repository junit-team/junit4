package org.junit.internal.runners.statements;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;

public class ExpectExceptionTest {

    @Test
    public void givenWeAreExpectingAssumptionViolatedExceptionAndAStatementThrowingAssumptionViolatedException() throws Exception {
        ExpectException sut = new ExpectException(new StatementThrowingAssumptionViolatedException(), AssumptionViolatedException.class);

        sut.evaluate();

        // then no exception should be thrown
    }


    private static class StatementThrowingAssumptionViolatedException extends Statement {
        @Override
        public void evaluate() throws Throwable {
            throw new AssumptionViolatedException("expected");
        }
    }
}