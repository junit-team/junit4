package org.junit.internal.requests;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;

/**
 * A filtered {@link Request}.
 */
public final class FilterRequest extends Request {
    private final Request request;
    private final Filter filter;

    /**
     * Creates a filtered Request
     *
     * @param classRequest a {@link Request} describing your Tests
     * @param filter {@link Filter} to apply to the Tests described in
     * <code>classRequest</code>
     */
    public FilterRequest(Request classRequest, Filter filter) {
        request = classRequest;
        this.filter = filter;
    }

    @Override
    public Runner getRunner() {
        try {
            Runner runner = request.getRunner();
            filter.apply(runner);
            return runner;
        } catch (NoTestsRemainException e) {
            return new ErrorReportingRunner(Filter.class, new Exception(String
                    .format("No tests found matching %s from %s", filter
                            .describe(), request.toString())));
        }
    }
}