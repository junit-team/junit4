/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.experimental.theories.FailureListener;

public class ExpectingException extends Link {
	private Link fNext;

	private final Class<? extends Throwable> fExpected;

	public ExpectingException(Link next, Class<? extends Throwable> expected) {
		fNext= next;
		fExpected= expected;
	}

	@Override
	public void run(final FailureListener listener) {
		FailureListener expectingListener= new FailureListener() {
			@Override
			public void handleFailure(Throwable error) {
				if (!fExpected.isAssignableFrom(error.getClass())) {
					String message= "Unexpected exception, expected<"
							+ fExpected.getName() + "> but was<"
							+ error.getClass().getName() + ">";
					listener.addFailure(new Exception(message, error));
				}
			}
		};
		
		fNext.run(expectingListener);
		
		if (!expectingListener.failureSeen())
			listener.addFailure(new AssertionError("Expected exception: "
					+ fExpected.getName()));
	}
}