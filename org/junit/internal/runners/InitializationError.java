package org.junit.internal.runners;

import java.util.Arrays;
import java.util.List;

public class InitializationError extends Exception {
	private static final long serialVersionUID= 1L;
	private final List<Throwable> fErrors;

	public InitializationError(List<Throwable> errors) {
		fErrors= errors;
	}

	public InitializationError(Throwable... errors) {
		this(Arrays.asList(errors));
	}
	
	public InitializationError(String string) {
		this(new Exception(string));
	}

	public List<Throwable> getCauses() {
		return fErrors;
	}
}
