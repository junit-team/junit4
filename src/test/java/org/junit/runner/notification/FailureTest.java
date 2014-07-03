package org.junit.runner.notification;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;

import org.junit.Test;
import org.junit.runner.Description;

public class FailureTest {
    private static final Description DESCRIPTION = Description
            .createSuiteDescription("dummy name");

    private static final Throwable EXCEPTION_MOCK = new Throwable() {
        @Override
        public String getMessage() {
            return "dummy message";
        }

        @Override
        public void printStackTrace(PrintWriter writer) {
            writer.write("dummy stack trace");
        }
    };

    private final Failure failure = new Failure(DESCRIPTION, EXCEPTION_MOCK);

    @Test
    public void usesDisplayNameAsTestHeader() {
        assertThat(failure.getTestHeader(), is(equalTo("dummy name")));
    }

    @Test
    public void providesDescription() {
        assertThat(failure.getDescription(), is(equalTo(DESCRIPTION)));
    }

    @Test
    public void providesThrownException() {
        assertThat(failure.getException(), is(sameInstance(EXCEPTION_MOCK)));
    }

    @Test
    public void hasStringRepresentationWithDescriptionsDisplayNameAndExceptionsMessage() {
        assertThat(failure.toString(), is(equalTo("dummy name: dummy message")));
    }

    @Test
    public void providesExceptionsStackTrace() {
        assertThat(failure.getTrace(), is(equalTo("dummy stack trace")));
    }

    @Test
    public void providesExceptionsMessage() {
        assertThat(failure.getMessage(), is(equalTo("dummy message")));
    }
}