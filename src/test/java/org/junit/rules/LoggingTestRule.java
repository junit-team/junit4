package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

class LoggingTestRule implements TestRule {
    private final StringBuilder log;
    private final String name;

    LoggingTestRule(StringBuilder log, String name) {
        this.name = name;
        this.log = log;
    }

    public Statement apply(Statement base, Description description) {
        return new LoggingStatement(base, log, name);
    }
}
