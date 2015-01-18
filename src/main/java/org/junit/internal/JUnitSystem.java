package org.junit.internal;

import java.io.PrintStream;

public interface JUnitSystem {

    /**
     * Will be removed in the next major release
     */
    @Deprecated
    void exit(int code);

    PrintStream out();
}
