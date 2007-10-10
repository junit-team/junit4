/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.experimental.theories.FailureListener;
import org.junit.internal.runners.model.TestMethod;

public class Invoke extends Link {
	private final TestMethod fTestMethod;
	private Object fTarget;
	
	public Invoke(TestMethod testMethod, Object target) {
		fTestMethod= testMethod;
		fTarget= target;
	}
	
	@Override
	public void run(FailureListener listener) {
		try {
			fTestMethod.invokeExplosively(fTarget);
		} catch (Throwable e) {
			listener.addFailure(e);
		}
	}
}