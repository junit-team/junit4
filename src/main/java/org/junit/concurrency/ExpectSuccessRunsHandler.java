package org.junit.concurrency;

import org.junit.runners.model.FrameworkMethod;

/**
 * Handler for the parameters {@link Concurrency#expectMinimumSuccessRuns()}
 * and {@link Concurrency#expectMaximumSuccessRuns()).
 * 
 * @author Christoph Jerolimov
 */
public class ExpectSuccessRunsHandler {
	
	private RunnerNotifyHandler runnerNotifyHandler;
	
	private int expectMinimumSuccessRuns = -1;
	private int expectMaximumSuccessRuns = -1;
	
	private volatile int successRuns = 0;
	
	public ExpectSuccessRunsHandler(RunnerNotifyHandler runnerNotifyHandler, FrameworkMethod method) {
		this.runnerNotifyHandler = runnerNotifyHandler;
		
		Concurrency methodAnnotation = method.getAnnotation(Concurrency.class);
		if (methodAnnotation != null) {
			expectMinimumSuccessRuns = methodAnnotation.expectMinimumSuccessRuns();
			expectMaximumSuccessRuns = methodAnnotation.expectMaximumSuccessRuns();
		}
	}
	
	public void handleSuccess() {
		successRuns++;
	}

	public void fireTestFinished() {
		if (expectMinimumSuccessRuns != -1 && successRuns < expectMinimumSuccessRuns) {
			runnerNotifyHandler.handleException(new AssertionError(
					"Too few successful runs! " + successRuns + " runs are successful but " +
					expectMinimumSuccessRuns + " are at least required."));
		}
		if (expectMaximumSuccessRuns != -1 && successRuns > expectMaximumSuccessRuns) {
			runnerNotifyHandler.handleException(new AssertionError(
					"Too many successful runs! " + successRuns + " runs are successful but " +
					expectMaximumSuccessRuns + " are maximal allowed."));
		}
	}
}
