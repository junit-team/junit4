package org.junit.internal.runners.links;

import org.junit.experimental.theories.FailureListener;

public class ThrowException extends Link {

	private final Throwable fError;

	public ThrowException(Throwable error) {
		fError= error;
	}

	@Override
	public void run(FailureListener listener) {
		listener.addFailure(fError);
	}

}
