/**
 * 
 */
package org.junit.experimental.interceptor;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class Interceptors extends BlockJUnit4ClassRunner {
	public Interceptors(Class<?> klass) throws InitializationError {
		super(klass);
	}
}