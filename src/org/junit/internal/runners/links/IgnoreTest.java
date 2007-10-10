/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.experimental.theories.FailureListener;
import org.junit.internal.runners.model.EachTestNotifier;


public class IgnoreTest extends Link {
	private final EachTestNotifier fNotifier;

	public IgnoreTest(EachTestNotifier notifier) {
		fNotifier= notifier;
	}
	
	@Override
	public void run(FailureListener listener) {
		fNotifier.fireTestIgnored();
	}
}