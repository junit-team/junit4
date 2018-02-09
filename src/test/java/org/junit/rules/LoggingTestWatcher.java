package org.junit.rules;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;

class LoggingTestWatcher extends TestWatcher {
    private final StringBuilder log;

    LoggingTestWatcher(StringBuilder log) {
        this.log = log;
    }

    @Override
    protected void succeeded(Description description) {
        log.append("succeeded ");
    }

    @Override
    protected void failed(Throwable e, Description description) {
        log.append("failed ");
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        log.append("skipped ");
    }

    @Override
    protected void skipped(org.junit.internal.AssumptionViolatedException e, Description description) {
        log.append("deprecated skipped ");
    }

    @Override
    protected void starting(Description description) {
        log.append("starting ");
    }

    @Override
    protected void finished(Description description) {
        log.append("finished ");
    }
}