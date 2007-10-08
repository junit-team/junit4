/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.Roadie;
import org.junit.internal.runners.model.TestMethod;

public class Invoke extends Link {
	private final TestMethod fTestMethod;
	
	public Invoke(TestMethod testMethod) {
		fTestMethod= testMethod;
	}
	
	@Override
	public void run(Roadie context) throws Throwable {
		fTestMethod.invokeExplosively(context.getTarget());
	}
}