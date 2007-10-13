package org.junit.internal.runners.model;

import java.util.ArrayList;
import java.util.List;

public class MultipleFailureException extends Exception {
	private static final long serialVersionUID= 1L;
	
	private List<Throwable> fErrors = new ArrayList<Throwable>();
	
	public void assertEmpty() throws Throwable {
		if (fErrors.isEmpty())
			return;
		if (fErrors.size() == 1)
			throw fErrors.get(0);
		throw this;
	}

	public void add(Throwable e) {
		fErrors.add(e);
	}

	public List<Throwable> getFailures() {
		return fErrors;
	}
}
