package org.junit.internal;

import java.io.PrintStream;

public class RealSystem implements JUnitSystem {
    public PrintStream out() {
        return System.out;
    }

}
