package org.junit.tests.experimental.theories.internal;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.experimental.theories.internal.SpecificDataPointsSupplier;
import org.junit.runners.model.TestClass;

public class SpecificDataPointsSupplierTest {

    public static class TestClassWithNamedDataPoints {

        @DataPoints({"field", "named"})
        public static String[] values = new String[] { "named field" };

        @DataPoints
        public static String[] otherValues = new String[] { "other" };
        
        @DataPoints({"method", "named"})
        public static String[] getValues() {
            return new String[] { "named method" };
        }
        
        @DataPoint({"single", "named"})
        public static String singleValue = "named single value";
        
        @DataPoint
        public static String otherSingleValue = "other value";
        
        @DataPoint({"singlemethod", "named"})
        public static String getSingleValue() { 
            return "named single method value";
        }
        
        @DataPoint
        public static String getSingleOtherValue() {
            return "other single method value";
        }
         
        @DataPoints
        public static String[] getOtherValues() {
            return new String[] { "other method" };
        }
    }

    @Test
    public void shouldReturnOnlyTheNamedDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(TestClassWithNamedDataPoints.class));

        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingAllNamedStrings"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);

        assertEquals(4, assignedStrings.size());
        assertThat(assignedStrings, hasItems("named field", "named method", "named single value", "named single method value"));
    }
    
    @Test
    public void shouldReturnOnlyTheNamedFieldDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(TestClassWithNamedDataPoints.class));

        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingNamedFieldString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);

        assertEquals(1, assignedStrings.size());
        assertThat(assignedStrings, hasItem("named field"));
    }

    @Test
    public void shouldReturnOnlyTheNamedMethodDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(TestClassWithNamedDataPoints.class));

        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingNamedMethodString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);

        assertEquals(1, assignedStrings.size());
        assertThat(assignedStrings, hasItem("named method"));
    }
    
    @Test
    public void shouldReturnOnlyTheNamedSingleFieldDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(TestClassWithNamedDataPoints.class));

        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingNamedSingleFieldString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);

        assertEquals(1, assignedStrings.size());
        assertThat(assignedStrings, hasItem("named single value"));
    }

    @Test
    public void shouldReturnOnlyTheNamedSingleMethodDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(TestClassWithNamedDataPoints.class));

        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingNamedSingleMethodString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);

        assertEquals(1, assignedStrings.size());
        assertThat(assignedStrings, hasItem("named single method value"));
    }    
    
    @Test
    public void shouldReturnNothingIfTheNamedDataPointsAreMissing() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(TestClassWithNamedDataPoints.class));

        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingWrongNamedString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);

        assertEquals(0, assignedStrings.size());
    }

    private List<String> getStringValuesFromAssignments(List<PotentialAssignment> assignments) throws CouldNotGenerateValueException {
        List<String> stringValues = new ArrayList<String>();
        for (PotentialAssignment assignment : assignments) {
            stringValues.add((String) assignment.getValue());
        }
        return stringValues;
    }

    private ParameterSignature signature(String methodName) throws Exception {
        return ParameterSignature.signatures(this.getClass().getMethod(methodName, String.class)).get(0);
    }

    public void methodWantingAnyString(String input) {
    }

    public void methodWantingNamedFieldString(@FromDataPoints("field") String input) {
    }
    
    public void methodWantingNamedMethodString(@FromDataPoints("method") String input) {
    }
    
    public void methodWantingNamedSingleFieldString(@FromDataPoints("single") String input) {
    }
    
    public void methodWantingNamedSingleMethodString(@FromDataPoints("singlemethod") String input) {
    }
    
    public void methodWantingAllNamedStrings(@FromDataPoints("named") String input) {
    }

    public void methodWantingWrongNamedString(@FromDataPoints("invalid name") String input) {
    }

}
