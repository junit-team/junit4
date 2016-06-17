package org.junit.runners.model;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class InvalidTestClassErrorTest {

    @Test
    public void invalidTestClassErrorShouldListAllValidationErrorsInItsMessage() {
        InvalidTestClassError sut = new InvalidTestClassError(SampleTestClass.class,
                asList(new Throwable("validation error 1"), new Throwable("validation error 2")));

        assertThat(sut.getMessage(), equalTo("Invalid test class '" + SampleTestClass.class.getName() + "':" +
                "\n  1. validation error 1" +
                "\n  2. validation error 2"));
    }

    private static class SampleTestClass {
    }
}