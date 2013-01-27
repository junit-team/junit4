package org.junit.tests.experimental.theories.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.internal.AllMembersSupplier;
import org.junit.runners.model.TestClass;

public class AllMembersSupplierTest {
    public static class HasDataPoints {
        @DataPoints
        public static Object[] objects = {1, 2};

        public HasDataPoints(Object obj) {
        }
    }

    @Test
    public void dataPointsAnnotationMeansTreatAsArrayOnly()
            throws SecurityException, NoSuchMethodException {
        List<PotentialAssignment> valueSources = new AllMembersSupplier(
                new TestClass(HasDataPoints.class))
                .getValueSources(ParameterSignature.signatures(
                        HasDataPoints.class.getConstructor(Object.class))
                        .get(0));
        assertThat(valueSources.size(), is(2));
    }

    public static class HasDataPointsFieldWithNullValue {
        @DataPoints
        public static Object[] objects = {null, "a"};

        public HasDataPointsFieldWithNullValue(Object obj) {
        }
    }

    @Test
    public void dataPointsArrayFieldMayContainNullValue()
            throws SecurityException, NoSuchMethodException {
        List<PotentialAssignment> valueSources = new AllMembersSupplier(
                new TestClass(HasDataPointsFieldWithNullValue.class))
                .getValueSources(ParameterSignature.signatures(
                        HasDataPointsFieldWithNullValue.class.getConstructor(Object.class))
                        .get(0));
        assertThat(valueSources.size(), is(2));
    }

    public static class HasDataPointsMethodWithNullValue {
        @DataPoints
        public static Integer[] getObjects() {
            return new Integer[] {null, 1};
        }

        public HasDataPointsMethodWithNullValue(Integer i) {
        }
    }

    @Test
    public void dataPointsArrayMethodMayContainNullValue()
            throws SecurityException, NoSuchMethodException {
        List<PotentialAssignment> valueSources = new AllMembersSupplier(
                new TestClass(HasDataPointsMethodWithNullValue.class))
                .getValueSources(ParameterSignature.signatures(
                        HasDataPointsMethodWithNullValue.class.getConstructor(Integer.class))
                        .get(0));
        assertThat(valueSources.size(), is(2));
    }
}
