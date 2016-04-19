package org.junit.internal.runners;

import org.junit.Test;

public class ErrorReportingRunnerTest {
    
    @Test(expected = NullPointerException.class)
    public void cannotCreateWithNullClass() {
        new ErrorReportingRunner(null, new RuntimeException());
    }
    
    @Test(expected = NullPointerException.class)
    public void cannotCreateWithNullClass2() {
        new ErrorReportingRunner(new RuntimeException(), (Class<?>) null);
    }
    
    @Test(expected = NullPointerException.class)
    public void cannotCreateWithNullClasses() {
        new ErrorReportingRunner(new RuntimeException(), (Class<?>[]) null);
    }
    
    @Test(expected = NullPointerException.class)
    public void cannotCreateWithoutClass() {
        new ErrorReportingRunner(new RuntimeException());
    }
    
}
