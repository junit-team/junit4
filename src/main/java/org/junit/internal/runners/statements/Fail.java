package org.junit.internal.runners.statements;

import org.junit.runners.model.Statement;

public class Fail extends Statement {
    private final Throwable error;

    public Fail(Throwable e) {
        error = e;
    }

    @Override
    public void evaluate() throws Throwable {
        throw error;
    }
}
