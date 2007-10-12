package org.junit.internal;

import java.io.PrintStream;

public interface JUnitSystem {
	void exit(int i);
	PrintStream out();
}
