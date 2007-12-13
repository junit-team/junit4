package org.junit.internal.requests;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runners.Suite;

public class ClassesRequest extends Request {
	private final Class<?>[] fClasses;
	private final String fName;
	
	public ClassesRequest(String name, Class<?>... classes) {
		fClasses= classes;
		fName= name;
	}

	/** @inheritDoc */
	@Override 
	public Runner getRunner() {
		try {
			return new Suite(fName, fClasses);
		} catch (InitializationError e) {
			// TODO: (Dec 10, 2007 9:13:13 PM) untested

			return Request.errorReport(null, e).getRunner();
		}
	}
}