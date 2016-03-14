package org.junit.tests;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.internal.JUnitSystem;

public class TestSystem implements JUnitSystem {
    private PrintStream out;
    public int fCode;
    private ByteArrayOutputStream fOutContents;

    public TestSystem() {
        fOutContents = new ByteArrayOutputStream();
        out = new PrintStream(fOutContents);
    }

    /**
     * Will be removed in the next major release
     */
    @Deprecated
    public void exit(int code) {
        fCode = code;
    }

    public PrintStream out() {
        return out;
    }

    public OutputStream outContents() {
        return fOutContents;
    }

}
