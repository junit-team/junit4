/**
 * 
 */
package org.junit.internal.runners.statements;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.runners.model.Statement;


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
				try {
					fNext.evaluate();
				} catch (Throwable e) {
					throw new ExecutionException(e);
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
			throw unwrap(e);
		}
	}

	private Throwable unwrap(Throwable e) {
		if (e instanceof ExecutionException)
			return unwrap(e.getCause());
		return e;
	}
}