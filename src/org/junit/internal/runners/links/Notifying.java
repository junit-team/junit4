/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.experimental.theories.FailureListener;
import org.junit.internal.runners.model.EachTestNotifier;

public class Notifying extends Link {
	private final Link fNext;

	private final EachTestNotifier fNotifier;

	public Notifying(EachTestNotifier notifier, Link next) {
		fNotifier= notifier;
		fNext= next;
	}

	@Override
	public void run(FailureListener listener) {
		fNotifier.fireTestStarted();
		fNext.run(listener);
		fNotifier.fireTestFinished();
	}
}