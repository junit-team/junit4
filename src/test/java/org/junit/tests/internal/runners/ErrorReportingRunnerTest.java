package org.junit.tests.internal.runners;

import org.junit.Test;
import org.junit.internal.runners.ErrorReportingRunner;

public class ErrorReportingRunnerTest {
    @Test(expected = NullPointerException.class)
    public void cannotCreateWithNullClass() {
        new ErrorReportingRunner(null, new RuntimeException());
    }
}
