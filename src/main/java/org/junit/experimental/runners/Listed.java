/**
 * 
 */
package org.junit.experimental.runners;

import java.util.Arrays;
import java.util.List;

public class Listed implements SuiteBuilder.Classes.Value {
	private final Class<?>[] fClasses;

	public Listed(Class<?>... classes) {
		fClasses= classes;
	}

	public List<? extends Class<?>> get() {
		return Arrays.asList(fClasses);
	}
}