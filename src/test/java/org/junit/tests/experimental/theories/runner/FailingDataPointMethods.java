package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

public class FailingDataPointMethods {
    
    @RunWith(Theories.class)
    public static class HasFailingSingleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoint
        public static int failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }

    @Test
    public void shouldFailFromExceptionsInSingleDataPointMethods() {
        assertThat(testResult(HasWronglyIgnoredFailingSingleDataPointMethod.class), not(isSuccessful()));
    }
    
    @RunWith(Theories.class)
    public static class HasFailingDataPointArrayMethod {
        @DataPoints
        public static int[] num = { 1, 2, 3 };

        @DataPoints
        public static int[] failingDataPoints() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }

    @Test
    public void shouldFailFromExceptionsInDataPointArrayMethods() {
        assertThat(testResult(HasFailingDataPointArrayMethod.class), not(isSuccessful()));
    }
    
    @RunWith(Theories.class)
    public static class HasIgnoredFailingSingleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoint(ignoredExceptions=Throwable.class)
        public static int failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }
    
    @Test
    public void shouldIgnoreSingleDataPointMethodExceptionsOnRequest() {
        assertThat(testResult(HasIgnoredFailingSingleDataPointMethod.class), isSuccessful());
    }
    
    @RunWith(Theories.class)
    public static class HasIgnoredFailingMultipleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoints(ignoredExceptions=Throwable.class)
        public static int[] failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }
    
    @Test
    public void shouldIgnoreMultipleDataPointMethodExceptionsOnRequest() {
        assertThat(testResult(HasIgnoredFailingMultipleDataPointMethod.class), isSuccessful());
    }
    
    @RunWith(Theories.class)
    public static class HasWronglyIgnoredFailingSingleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoint(ignoredExceptions=NullPointerException.class)
        public static int failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }    
    
    @Test
    public void shouldNotIgnoreNonMatchingSingleDataPointExceptions() {
        assertThat(testResult(HasWronglyIgnoredFailingSingleDataPointMethod.class), not(isSuccessful()));
    }
    
    @RunWith(Theories.class)
    public static class HasWronglyIgnoredFailingMultipleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoint(ignoredExceptions=NullPointerException.class)
        public static int failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }    
    
    @Test
    public void shouldNotIgnoreNonMatchingMultipleDataPointExceptions() {
        assertThat(testResult(HasWronglyIgnoredFailingMultipleDataPointMethod.class), not(isSuccessful()));
    }
    
}
