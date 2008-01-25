package org.junit.internal.runners.model;

import java.util.List;

public class MultipleFailureException extends Exception {
	private static final long serialVersionUID= 1L;
	
	private final List<Throwable> fErrors;

	public MultipleFailureException(List<Throwable> errors) {
		fErrors= errors;
	}

	public List<Throwable> getFailures() {
		return fErrors;
	}
}
