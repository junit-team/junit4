package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import static org.junit.tests.experimental.theories.TheoryTestUtils.potentialAssignments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

public class WithDataPointMethod {
    @RunWith(Theories.class)
    public static class HasDataPointMethod {
        @DataPoint
        public static int oneHundred() {
            return 100;
        }

        @Theory
        public void allIntsOk(int x) {
        }
    }

    @Test
    public void pickUpDataPointMethods() {
        assertThat(testResult(HasDataPointMethod.class), isSuccessful());
    }

    @RunWith(Theories.class)
    public static class DataPointMethodReturnsMutableObject {
        @DataPoint
        public static List<Object> empty() {
            return new ArrayList<Object>();
        }

        @DataPoint
        public static int ONE = 1;

        @DataPoint
        public static int TWO = 2;

        @Theory
        public void everythingsEmpty(List<Object> first, int number) {
            assertThat(first.size(), is(0));
            first.add("a");
        }
    }

    @Test
    public void mutableObjectsAreCreatedAfresh() {
        assertThat(failures(DataPointMethodReturnsMutableObject.class), empty());
    }

    @RunWith(Theories.class)
    public static class HasDateMethod {
        @DataPoint
        public static int oneHundred() {
            return 100;
        }

        public static Date notADataPoint() {
            return new Date();
        }

        @Theory
        public void allIntsOk(int x) {
        }

        @Theory
        public void onlyStringsOk(String s) {
        }

        @Theory
        public void onlyDatesOk(Date d) {
        }
    }

    @Test
    public void ignoreDataPointMethodsWithWrongTypes() throws Throwable {
        assertThat(potentialAssignments(
                HasDateMethod.class.getMethod("onlyStringsOk", String.class))
                .toString(), not(containsString("100")));
    }

    @Test
    public void ignoreDataPointMethodsWithoutAnnotation() throws Throwable {
        assertThat(potentialAssignments(
                HasDateMethod.class.getMethod("onlyDatesOk", Date.class))
                .size(), is(0));
    }

    private List<Failure> failures(Class<?> type) {
        return JUnitCore.runClasses(type).getFailures();
    }

    private Matcher<Iterable<Failure>> empty() {
        return everyItem(nullValue(Failure.class));
    }
}
