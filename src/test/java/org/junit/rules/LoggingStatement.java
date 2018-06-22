package org.junit.rules;

import org.junit.runners.model.Statement;

class LoggingStatement extends Statement {
    private final Statement base;
    private final StringBuilder log;
    private final String name;

    LoggingStatement(Statement base, StringBuilder log, String name) {
        this.base = base;
        this.log = log;
        this.name = name;
    }

    public void evaluate() throws Throwable {
        log.append(name).append(".begin ");
        try {
            base.evaluate();
        } finally {
            log.append(name).append(".end ");
        }
    }
}
