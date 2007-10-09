package org.junit.internal.runners.model;

import java.util.ArrayList;
import java.util.List;


public class ErrorList {
	private final List<Throwable> fErrors= new ArrayList<Throwable>();

	// TODO this appears to be the only shared method, otherwise we could just go back to using a Collection
	// if we want sharing, we could put this method in Runner, which all current callers (JUnit4ClassRunner, Parameterized, and Suite) 
	// all inherit from
	public void assertEmpty() throws InitializationError {
		if (!fErrors.isEmpty())
			throw new InitializationError(fErrors);
	}
	
	public void add(Throwable e) {
		fErrors.add(e);
	}

	public boolean isEmpty() {
		return fErrors.isEmpty();
	}
}
