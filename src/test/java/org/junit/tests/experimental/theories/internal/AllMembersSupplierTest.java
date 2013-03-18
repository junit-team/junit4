package org.junit.tests.experimental.theories.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.tests.experimental.theories.TheoryTestUtils.potentialAssignments;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.internal.AllMembersSupplier;
import org.junit.runners.model.TestClass;

public class AllMembersSupplierTest {
    
    public static class HasDataPointsArrayField {
        @DataPoints
        public static String[] list = new String[] { "qwe", "asd" };

        @Theory
        public void theory(String param) {
        }
    }
    
    @Test
    public void dataPointsArrayShouldBeRecognized() throws Exception {
        List<PotentialAssignment> assignments = potentialAssignments(
                HasDataPointsArrayField.class.getMethod("theory", String.class));
        
        assertEquals(2, assignments.size());
    }
    
    public static class HasDataPointsArrayWithMatchingButInaccurateTypes {
        @DataPoints
        public static Object[] objects = {1, "string!", 2};

        @Theory
        public void theory(Integer param) {
        }
    }

    @Test
    public void dataPointsArrayShouldBeRecognizedOnValueTypeNotFieldType() throws Exception {
        List<PotentialAssignment> assignments = potentialAssignments(
                HasDataPointsArrayWithMatchingButInaccurateTypes.class.getMethod("theory", Integer.class));
        
        assertEquals(2, assignments.size());
    }
    
    public static class HasDataPointMethodWithOverlyGeneralTypes {
        @DataPoint
        public static Integer object() {
            return 1;
        }

        @Theory
        public void theory(Object param) {
        }
    }

    @Test
    public void dataPointMethodShouldBeRecognizedForOverlyGeneralParameters() throws Exception {
        List<PotentialAssignment> assignments = potentialAssignments(
                HasDataPointMethodWithOverlyGeneralTypes.class.getMethod("theory", Object.class));
        
        assertEquals(1, assignments.size());
    }
    
    public static class HasDataPointsWithObjectParameter {
        @DataPoints
        public static Object[] objectField = {1, 2};

        @Theory
        public void theory(Object obj) {
        }
    }

    @Test
    public void dataPointsAnnotationMeansTreatAsArrayOnly() throws Exception {
        List<PotentialAssignment> assignments = potentialAssignments(
                HasDataPointsWithObjectParameter.class.getMethod("theory", Object.class));
        
        assertEquals(2, assignments.size());
        for (PotentialAssignment assignment : assignments) {
            assertNotEquals(HasDataPointsWithObjectParameter.objectField, assignment.getValue());
        }
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
