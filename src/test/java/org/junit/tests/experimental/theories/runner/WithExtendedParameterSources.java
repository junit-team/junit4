package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import org.junit.Test;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

public class WithExtendedParameterSources {
    @RunWith(Theories.class)
    public static class ParameterAnnotations {
        @Theory
        public void everythingIsOne(@TestedOn(ints = {1})
        int number) {
            assertThat(number, is(1));
        }
    }

    @Test
    public void testedOnLimitsParameters() throws Exception {
        assertThat(testResult(ParameterAnnotations.class), ResultMatchers
                .isSuccessful());
    }

    @RunWith(Theories.class)
    public static class ShouldFilterOutNullSingleDataPoints {

        @DataPoint
        public static String A = "a";
        
        @DataPoint
        public static String NULL = null;

        @Theory(nullsAccepted = false)
        public void allStringsAreNonNull(String s) {
            assertThat(s, notNullValue());
        }
    }

    @Test
    public void shouldFilterOutNullSingleDataPoints() {
        assertThat(testResult(ShouldFilterOutNullSingleDataPoints.class), isSuccessful());
    }

    @RunWith(Theories.class)
    public static class ShouldFilterOutNullElementsFromDataPointArrays {
        @DataPoints
        public static String[] SOME_NULLS = { "non-null", null };

        @Theory(nullsAccepted = false)
        public void allStringsAreNonNull(String s) {
            assertThat(s, notNullValue());
        }
    }

    @Test
    public void shouldFilterOutNullElementsFromDataPointArrays() {
        assertThat(testResult(ShouldFilterOutNullElementsFromDataPointArrays.class), isSuccessful());
    }
    
    @RunWith(Theories.class)
    public static class ShouldRejectTheoriesWithOnlyDisallowedNullData {
        @DataPoints
        public static String value = null;

        @Theory(nullsAccepted = false)
        public void allStringsAreNonNull(String s) {
        }
    }

    @Test
    public void ShouldRejectTheoriesWithOnlyDisallowedNullData() {
        assertThat(testResult(ShouldRejectTheoriesWithOnlyDisallowedNullData.class), not(isSuccessful()));
    }    

    @RunWith(Theories.class)
    public static class DataPointArrays {
        public static String log = "";

        @DataPoints
        public static String[] STRINGS = new String[]{"A", "B"};

        @Theory
        public void addToLog(String string) {
            log += string;
        }
    }

    @Test
    public void getDataPointsFromArray() {
        DataPointArrays.log = "";
        JUnitCore.runClasses(DataPointArrays.class);
        assertThat(DataPointArrays.log, is("AB"));
    }

    @RunWith(Theories.class)
    public static class DataPointArrayMethod {
        public static String log = "";

        @DataPoints
        public static String[] STRINGS() {
            return new String[]{"A", "B"};
        }

        ;

        @Theory
        public void addToLog(String string) {
            log += string;
        }
    }

    @Test
    public void getDataPointsFromArrayMethod() {
        DataPointArrayMethod.log = "";
        JUnitCore.runClasses(DataPointArrayMethod.class);
        assertThat(DataPointArrayMethod.log, is("AB"));
    }

    @RunWith(Theories.class)
    public static class DataPointMalformedArrayMethods {
        public static String log = "";

        @DataPoints
        public static String[] STRINGS() {
            return new String[]{"A", "B"};
        }

        ;

        @DataPoints
        public static String STRING() {
            return "C";
        }

        @DataPoints
        public static int[] INTS() {
            return new int[]{1, 2, 3};
        }

        @Theory
        public void addToLog(String string) {
            log += string;
        }
    }

    @Test
    public void getDataPointsFromArrayMethodInSpiteOfMalformedness() {
        DataPointArrayMethod.log = "";
        JUnitCore.runClasses(DataPointArrayMethod.class);
        assertThat(DataPointArrayMethod.log, is("AB"));
    }

    @RunWith(Theories.class)
    public static class DataPointArrayToBeUsedForWholeParameter {
        public static String log = "";

        @DataPoint
        public static String[] STRINGS = new String[]{"A", "B"};

        @Theory
        public void addToLog(String[] strings) {
            log += strings[0];
        }
    }

    @Test
    public void dataPointCanBeArray() {
        DataPointArrayToBeUsedForWholeParameter.log = "";
        JUnitCore.runClasses(DataPointArrayToBeUsedForWholeParameter.class);
        assertThat(DataPointArrayToBeUsedForWholeParameter.log, is("A"));
    }
}