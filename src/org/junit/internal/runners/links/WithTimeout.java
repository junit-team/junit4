/**
 * 
 */
package org.junit.internal.runners.links;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.experimental.theories.FailureListener;


public class WithTimeout extends Link {
	private Link fNext;
	private final long fTimeout;

	public WithTimeout(Link next, long timeout) {
		fNext= next;
		fTimeout= timeout;
	}

	@Override
	public void run(final FailureListener listener) {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				fNext.run(listener);
				return null;
			}
		};
		Future<Object> result= service.submit(callable);
		service.shutdown();
		try {
			boolean terminated= service.awaitTermination(fTimeout,
					TimeUnit.MILLISECONDS);
			if (!terminated)
				service.shutdownNow();
			result.get(0, TimeUnit.MILLISECONDS); // throws the exception if one occurred during the invocation
		} catch (TimeoutException e) {
			listener.addFailure(new Exception(String.format(
					"test timed out after %d milliseconds", fTimeout)));
		} catch (ExecutionException e) {
			listener.addFailure(e.getCause());
		} catch (InterruptedException e) {
			listener.addFailure(e);
		}
	}
}