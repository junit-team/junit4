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


public class FailOnTimeout extends Statement {
	private Statement fNext;
	private final long fTimeout;

	public FailOnTimeout(Statement next, long timeout) {
		fNext= next;
		fTimeout= timeout;
	}

	@Override
	public void evaluate() throws Throwable {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				// TODO: (Oct 12, 2007 10:15:08 AM) Use MultipleFailureException
				// TODO: (Oct 12, 2007 10:15:19 AM) Use MultipleFailureException in construction
				// TODO: (Oct 12, 2007 10:15:29 AM) Convert to Statement?



				try {
					fNext.evaluate();
				} catch (Exception e) {
					throw e;
				} catch (Error e) {
					throw e;
				} catch (Throwable e) {
					// TODO: (Oct 5, 2007 11:27:11 AM) Now what?  Is there a useful thing to do with this?
				}
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
			throw new Exception(String.format(
					"test timed out after %d milliseconds", fTimeout));
		} catch (ExecutionException e) {
			throw e.getCause();
		}
	}
}