/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.TestMethod;

public class InvokeMethod extends Statement {
	private final TestMethod fTestMethod;
	private Object fTarget;
	
	public InvokeMethod(TestMethod testMethod, Object target) {
		fTestMethod= testMethod;
		fTarget= target;
	}
	
	@Override
	public void evaluate() throws Throwable {
		fTestMethod.invokeExplosively(fTarget);
	}
}