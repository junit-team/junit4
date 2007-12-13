package org.junit.internal.requests;

import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runners.Suite;

public class ClassesRequest extends Request {
	// TODO: (Dec 13, 2007 2:29:43 AM) how useful are requests anymore?

	private final Class<?>[] fClasses;
	
	public ClassesRequest(Class<?>... classes) {
		fClasses= classes;
	}

	/** @inheritDoc */
	@Override 
	public Runner getRunner() {
		return new Suite(fClasses);
	}
}