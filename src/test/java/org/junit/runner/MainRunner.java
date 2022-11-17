package org.junit.runner;

import java.io.OutputStream;
import java.io.PrintStream;

public class MainRunner {

    public static int runMain(String... args) {
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(new NullOutputStream()));
        try {
            return JUnitCore.runMain(args);
        } finally {
            System.setOut(oldOut);
        }
    }

    static class NullOutputStream extends OutputStream {
        public void write(int b) {
            // do nothing
        }
    }
}
