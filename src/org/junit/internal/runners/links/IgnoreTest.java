/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.Roadie;

public class IgnoreTest extends Link {
	@Override
	public void run(Roadie context) throws Throwable {
		context.fireTestIgnored();
	}
}