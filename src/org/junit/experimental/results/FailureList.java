/**
 * 
 */
package org.junit.experimental.results;

import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class FailureList {
	private final List<Failure> failures;

	// TODO: (Jul 23, 2007 10:50:50 AM) This is a broad type. Can we take in
	// something that we're sure is not null, or containing null items?

	public FailureList(List<Failure> failures) {
		this.failures= failures;
	}

	public Result result() {
		Result result= new Result();
		RunListener listener= result.createListener();
		for (Failure failure : failures) {
			try {
				listener.testFailure(failure);
			} catch (Exception e) {
				throw new RuntimeException("I can't believe this happened");
			}
		}
		return result;
	}
}