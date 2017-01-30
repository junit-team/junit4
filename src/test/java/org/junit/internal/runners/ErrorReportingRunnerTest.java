package org.junit.internal.runners;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InvalidTestClassError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test
    public void givenInvalidTestClassErrorAsCause() {
        final List<Failure> firedFailures = new ArrayList<Failure>();
        InvalidTestClassError testClassError = new InvalidTestClassError(TestClassWithErrors.class,
                Arrays.asList(new Throwable("validation error 1"), new Throwable("validation error 2")));
        ErrorReportingRunner sut = new ErrorReportingRunner(TestClassWithErrors.class, testClassError);

        sut.run(new RunNotifier() {
            @Override
            public void fireTestFailure(Failure failure) {
                super.fireTestFailure(failure);
                firedFailures.add(failure);
            }
        });

        assertThat(firedFailures.size(), is(1));
        Throwable exception = firedFailures.get(0).getException();
        assertThat(exception, instanceOf(InvalidTestClassError.class));
        assertThat(((InvalidTestClassError) exception), is(testClassError));
    }

    @Test
    public void givenInvalidTestClass_integrationTest() {
        Result result = JUnitCore.runClasses(TestClassWithErrors.class);

        assertThat(result.getFailureCount(), is(1));
        Throwable failure = result.getFailures().get(0).getException();
        assertThat(failure, instanceOf(InvalidTestClassError.class));
        assertThat(failure.getMessage(), allOf(
                startsWith("Invalid test class '" + TestClassWithErrors.class.getName() + "'"),
                containsString("\n  1. "),
                containsString("\n  2. ")
        ));
    }

    private static class TestClassWithErrors {
        @Before public static void staticBeforeMethod() {}
        @After public static void staticAfterMethod() {}

        @Test public String testMethodReturningString() {
            return "this should not be allowed";
        }
    }
}
