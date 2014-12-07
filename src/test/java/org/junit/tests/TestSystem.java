package org.junit.tests;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.internal.JUnitSystem;

public class TestSystem implements JUnitSystem {

    private PrintStream out;
    private ByteArrayOutputStream outContents;

    public TestSystem() {
        outContents = new ByteArrayOutputStream();
        out = new PrintStream(outContents);
    }

    public PrintStream out() {
        return out;
    }

    public OutputStream outContents() {
        return outContents;
    }

}
