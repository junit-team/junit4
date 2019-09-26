package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

public class TypeMatchingBetweenMultiDataPointsMethod {

    @RunWith(Theories.class)
    public static class WithWrongfullyTypedDataPointsMethod {
        @DataPoint
        public static String[] correctlyTyped = {"Good", "Morning"};

        @DataPoints
        public static String[] wrongfullyTyped() {
            return new String[]{"Hello", "World"};
        }

        @Theory
        public void testTheory(String[] array) {
        }
    }

    @Test
    public void ignoreWrongTypedDataPointsMethod() {
        assertThat(testResult(WithWrongfullyTypedDataPointsMethod.class), isSuccessful());
    }

    @RunWith(Theories.class)
    public static class WithCorrectlyTypedDataPointsMethod {
        @DataPoint
        public static String[] correctlyTyped = {"Good", "Morning"};

        @DataPoints
        public static String[][] anotherCorrectlyTyped() {
            return new String[][]{
                    {"Hello", "World"}
            };
        }

        @Theory
        public void testTheory(String[] array) {
        }
    }

    @Test
    public void pickUpMultiPointDataPointMethods() throws Exception {
        assertThat(testResult(WithCorrectlyTypedDataPointsMethod.class), isSuccessful());
    }
}
