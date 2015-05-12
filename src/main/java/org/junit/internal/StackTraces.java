package org.junit.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for working with stack traces.
 */
public class StackTraces {
    private StackTraces() {
    }

    /**
     * Gets a trimmed version of the stack trace of the given exception. Stack trace
     * elements that are below the test method are filtered out.
     *
     * @return a trimmed stack trace, or the original trace if trimming wasn't possible
     */
    public static String getTrimmedStackTrace(Throwable exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        exception.printStackTrace(writer);
        return trimStackTrace(exception.toString(), stringWriter.toString());
    }

    /**
     * Trims the given stack trace.  Stack trace elements that are below the test method are
     * filtered. out.
     *
     * @param fullTrace the full stack trace
     * @return a trimmed stack trace, or the original trace if trimming wasn't possible
     */
    public static String trimStackTrace(String fullTrace) {
        return trimStackTrace("", fullTrace);
    }

    static String trimStackTrace(String extracedExceptionMessage, String fullTrace) {
        StringBuilder trimmedTrace = new StringBuilder(extracedExceptionMessage);
        BufferedReader reader = new BufferedReader(
            new StringReader(fullTrace.substring(extracedExceptionMessage.length())));

        try {
            // Collect the stack trace lines for "exception" (but not the cause).
            List<String> stackTraceLines = new ArrayList<String>();
            String line;
            boolean hasCause = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Caused by: ")) {
                    hasCause= true;
                    break;
                }
                stackTraceLines.add(line);
            }
            if (stackTraceLines.isEmpty()) {
                // No stack trace?
                return fullTrace;
            }
            stackTraceLines = trimStackTraceLines(stackTraceLines, hasCause);
            if (stackTraceLines.isEmpty()) {
                // Could not trim stack trace lines.
                return fullTrace;
            }
            appendStackTraceLines(stackTraceLines, trimmedTrace);
            if (line != null) {
                // Print remaining stack trace lines.
                do {
                    trimmedTrace.append(String.format("%s%n", line));
                    line = reader.readLine();
                } while (line != null);
            }
            return trimmedTrace.toString();
        } catch (IOException e) {
        }
        return fullTrace;
    }

    private static void appendStackTraceLines(
            List<String> stackTraceLines, StringBuilder destBuilder) {
        for (String stackTraceLine : stackTraceLines) {
            destBuilder.append(String.format("%s%n", stackTraceLine));
        }
    }

    private static List<String> trimStackTraceLines(
            List<String> stackTraceLines, boolean hasCause) {
        State state = State.PROCESSING_OTHER_CODE;
        int linesToInclude = stackTraceLines.size();
        for (String stackTraceLine : asReversedList(stackTraceLines)) {
            state = state.processLine(stackTraceLine);
            if (state == State.DONE) {
                List<String> trimmedLines = stackTraceLines.subList(0, linesToInclude);
                if (!hasCause) {
                    return trimmedLines;
                }
                List<String> copy = new ArrayList<String>(trimmedLines);
                copy.add("\t..." + (stackTraceLines.size() - copy.size()) + "more");
                return copy;
            }
            linesToInclude--;
        }
        return Collections.emptyList();
    }

    private static <T> List<T> asReversedList(final List<T> list) {
        return new AbstractList<T>() {

            @Override
            public T get(int index) {
                return list.get(list.size() - index - 1);
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    private enum State {
        PROCESSING_OTHER_CODE {
            @Override public State processLine(String line) {
                if (isTestFrameworkStackTraceLine(line)) {
                    return PROCESSING_TEST_FRAMEWORK_CODE;
                }
                return this;
            }
        },
        PROCESSING_TEST_FRAMEWORK_CODE {
            @Override public State processLine(String line) {
                if (isReflectionStackTraceLine(line)) {
                    return PROCESSING_REFLECTION_CODE;
                } else if (isTestFrameworkStackTraceLine(line)) {
                    return this;
                }
                return PROCESSING_OTHER_CODE;
            } 
        },
        PROCESSING_REFLECTION_CODE {
            @Override public State processLine(String line) {
                if (isReflectionStackTraceLine(line)) {
                    return this;
                } else if (isTestFrameworkStackTraceLine(line)) {
                    // This is here to handle TestCase.runBare() calling TestCase.runTest().
                    return PROCESSING_TEST_FRAMEWORK_CODE;
                }
                return DONE;
            } 
        },
        DONE {
            @Override public State processLine(String line) {
                return this;
            } 
        };

        /** Processes a stack trace line, possibly moving to a new state. */
        public abstract State processLine(String line);
    }

    private static final String[] TEST_FRAMEWORK_METHOD_NAME_PREFIXES = {
        "org.junit.runner.",
        "org.junit.runners.",
        "org.junit.experimental.runners.",
        "org.junit.internal.",
        "junit.",
        "org.apache.maven.surefire."
    };

    private static boolean isTestFrameworkStackTraceLine(String line) {
        return isMatchingStackTraceLine(line, TEST_FRAMEWORK_METHOD_NAME_PREFIXES);
    }
    
    private static final String[] REFLECTION_METHOD_NAME_PREFIXES = {
        "sun.reflect.",
        "java.lang.reflect.",
        "org.junit.rules.RunRules.evaluate(", // get better stack traces for failures in method rules
        "junit.framework.TestCase.runBare(", // runBare() directly calls setUp() and tearDown()
   };
    
    private static boolean isReflectionStackTraceLine(String line) {
        return isMatchingStackTraceLine(line, REFLECTION_METHOD_NAME_PREFIXES);
    }

    private static boolean isMatchingStackTraceLine(String line, String[] packagePrefixes) {
        if (!line.startsWith("\tat ")) {
            return false;
        }
        line = line.substring(4);
        for (String packagePrefix : packagePrefixes) {
            if (line.startsWith(packagePrefix)) {
                return true;
            }
        }
        
        return false;
    }
}
