package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RetryRule implements TestRule {
    private final RetryDecider retryDecider;

    public RetryRule(RetryDecider retryDecider) {
        this.retryDecider = retryDecider;
    }

    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                while (true) {
                    try {
                        base.evaluate();
                        retryDecider.reportSuccess();
                        return;
                    } catch (Throwable t) {
                        if (!retryDecider.reportFailure(t)) {
                            throw t;
                        }
                    }
                }
            }
        };
    }
}
