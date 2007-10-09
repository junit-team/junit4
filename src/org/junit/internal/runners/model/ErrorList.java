package org.junit.internal.runners.model;

import java.util.ArrayList;
import java.util.List;


public class ErrorList {
	private final List<Throwable> fErrors= new ArrayList<Throwable>();

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
