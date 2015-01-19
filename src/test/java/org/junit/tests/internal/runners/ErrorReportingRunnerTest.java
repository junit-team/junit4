package org.junit.tests.internal.runners;

import org.junit.Test;
import org.junit.internal.runners.ErrorReportingRunner;

public class ErrorReportingRunnerTest {

    @Test(expected = NullPointerException.class)
    public void cannotCreateWithCauseAndNullClass() {
        new ErrorReportingRunner((Class<?>) null, new RuntimeException());
    }

    @Test(expected = NullPointerException.class)
    public void cannotCreateWithCauseAndNullDisplayName() {
        new ErrorReportingRunner((String) null, new RuntimeException());
    }
}
