/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.Roadie;
import org.junit.internal.runners.model.TestMethod;

public class InvokeMethod extends Link {
	private final TestMethod fTestMethod;
	
	public InvokeMethod(TestMethod testMethod) {
		fTestMethod= testMethod;
	}
	
	@Override
	public void run(Roadie context) throws Throwable {
		fTestMethod.invokeExplosively(context.getTarget());
	}
}