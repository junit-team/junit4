package org.junit.internal;

import java.io.PrintStream;

public class RealSystem implements JUnitSystem {

	public void exit(int code) {
		System.exit(code);
	}

	public PrintStream out() {
		return System.out;
	}

}
