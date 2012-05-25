package org.junit.internal;

import java.io.PrintStream;

/**
 * @since 4.5
 */
public class RealSystem implements JUnitSystem {
	public PrintStream out() {
		return System.out;
	}

}
