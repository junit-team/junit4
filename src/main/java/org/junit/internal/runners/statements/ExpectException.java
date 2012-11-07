package org.junit.internal.runners.statements;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;

public class ExpectException extends Statement {
    private Statement fNext;
    private String fMessage;
    private final Class<? extends Throwable> fExpected;

    public ExpectException(Statement next, Class<? extends Throwable> expected, String message) {
        fNext = next;
        fExpected = expected;
        fMessage = message;
    }

    @Override
    public void evaluate() throws Exception {
        boolean complete = false;
        try {
            fNext.evaluate();
            complete = true;
        } catch (AssumptionViolatedException e) {
            throw e;
        } catch (Throwable e) {
            if (!fExpected.isAssignableFrom(e.getClass())) {
            	String message;
            	
            	if ( isMessageEmpty() ) {
            		message = "Unexpected exception, expected<"
                            + fExpected.getName() + "> but was<"
                            + e.getClass().getName() + ">";
            	} else {
            		message = fMessage;
            	}
            	
                throw new Exception(message, e);
            }
        }
        if (complete) {
        	String message;
        	if ( isMessageEmpty() ) {
        		message = "Expected exception: "
        				+ fExpected.getName();
        	} else {
        		message = fMessage;
        	}
        	
            throw new AssertionError(message);
        }
    }
    
    private boolean isMessageEmpty() {
    	return fMessage == null || fMessage.isEmpty();
    }
}