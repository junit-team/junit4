package org.junit.concurrency;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.junit.runners.model.FrameworkMethod;

/**
 * Handler for the parameter {@link Concurrency#expectAtLeast()).
 * 
 * @author Christoph Jerolimov
 */
public class ExpectAtLeastHandler {
	
	private RunnerNotifyHandler runnerNotifyHandler;
	
	private Class<? extends Throwable>[] expectAtLeast;

	private boolean exceptionHappened = false;
	
	public ExpectAtLeastHandler(RunnerNotifyHandler runnerNotifyHandler, FrameworkMethod method) {
		this.runnerNotifyHandler = runnerNotifyHandler;
		
		Concurrency methodAnnotation = method.getAnnotation(Concurrency.class);
		if (methodAnnotation != null && methodAnnotation.expectAtLeast().length != 0) {
			expectAtLeast = methodAnnotation.expectAtLeast();
		}
	}
	
	public void fireTestFinished() {
		if (expectAtLeast != null && !exceptionHappened) {
			runnerNotifyHandler.handleException(new AssertionError(
					"Missing at least one of these exceptions: " +
					Arrays.asList(expectAtLeast)));
		}
	}
	
	public void handleException(Throwable actualException) {
		// TODO logging?
//		System.out.println("expected " + expectAtLeast);
//		System.out.println("found " + actualException);
		if (expectAtLeast != null) {
			assertAtLeast(expectAtLeast, actualException);
			exceptionHappened = true;
		} else {
			runnerNotifyHandler.handleException(actualException);
		}
	}

	private void assertAtLeast(
			Class<? extends Throwable>[] expectAtLeast,
			Throwable actualException) {
		while (actualException instanceof ExecutionException) {
			actualException = actualException.getCause();
		}
		
		boolean foundException = false;
		for (Class<? extends Throwable> expect : expectAtLeast) {
			if (expect.isAssignableFrom(actualException.getClass())) {
				foundException = true;
			}
		}
		
		if (!foundException) {
			runnerNotifyHandler.handleException(actualException);
		}
	}
}
