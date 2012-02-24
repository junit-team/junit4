/**
 * 
 */
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
	private final Request fRequest;
	private final Filter fFilter;

	/**
	 * Creates a filtered Request
	 * @param classRequest a {@link Request} describing your Tests
	 * @param filter {@link Filter} to apply to the Tests described in 
	 * <code>classRequest</code>
	 */
	public FilterRequest(Request classRequest, Filter filter) {
		fRequest= classRequest;
		fFilter= filter;
	}

	@Override 
	public Runner getRunner() {
		try {
			Runner runner= fRequest.getRunner();
			fFilter.apply(runner);
			return runner;
		} catch (NoTestsRemainException e) {
			return new ErrorReportingRunner(Filter.class, new Exception(String
					.format("No tests found matching %s from %s", fFilter
							.describe(), fRequest.toString())));
		}
	}
}