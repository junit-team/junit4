package org.junit.experimental.theories;

public abstract class FailureListener {
	private boolean fFailureSeen = false;
	
	public final void addFailure(Throwable error) {
		fFailureSeen = true;
		handleFailure(error);
	}

	protected abstract void handleFailure(Throwable error);
	
	public boolean failureSeen() {
		return fFailureSeen;
	}
}
