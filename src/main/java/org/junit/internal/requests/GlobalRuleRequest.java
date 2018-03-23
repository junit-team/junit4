package org.junit.internal.requests;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.GlobalRuleRunner;

/**
 * A {@link Request} that adds global rules to the {@link Runner}.
 */
public final class GlobalRuleRequest extends Request {
    private final Request request;
    private final GlobalRuleRunner ruleRunner;

    /**
     * Creates a Request with global rules
     *
     * @param request a {@link Request} describing your Tests
     * @param ruleRunner {@link GlobalRuleRunner} to apply to the Tests described in
     * <code>request</code>
     */
    public GlobalRuleRequest(Request request, GlobalRuleRunner ruleRunner) {
        this.request = request;
        this.ruleRunner = ruleRunner;
    }

    @Override
    public Runner getRunner() {
        try {
            Runner runner = request.getRunner();
            ruleRunner.apply(runner);
            return runner;
        } catch (Exception e) {
            return new ErrorReportingRunner(GlobalRuleRunner.class, e);
        }
    }
}