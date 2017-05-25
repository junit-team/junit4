package org.junit.rules;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

class LoggingMethodRule implements MethodRule {
    private final StringBuilder log;
    private final String name;

    LoggingMethodRule(StringBuilder log, String name) {
        this.name = name;
        this.log = log;
    }

    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new LoggingStatement(base, log, name);
    }
}
