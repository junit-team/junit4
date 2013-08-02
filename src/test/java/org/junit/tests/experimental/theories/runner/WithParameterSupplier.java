package org.junit.tests.experimental.theories.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.tests.experimental.theories.TheoryTestUtils.potentialAssignments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class WithParameterSupplier {
    
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private static class SimplePotentialAssignment extends PotentialAssignment {
        private String description;
        private Object value;

        public SimplePotentialAssignment(Object value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public Object getValue() throws CouldNotGenerateValueException {
            return value;
        }

        @Override
        public String getDescription() throws CouldNotGenerateValueException {
            return description;
        }
    }

    private static final List<String> DATAPOINTS = Arrays.asList("qwe", "asd");

    public static class SimpleSupplier extends ParameterSupplier {

        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            List<PotentialAssignment> assignments = new ArrayList<PotentialAssignment>();

            for (String datapoint : DATAPOINTS) {
                assignments.add(new SimplePotentialAssignment(datapoint,
                        datapoint));
            }

            return assignments;
        }

    }

    @RunWith(Theories.class)
    public static class TestClassUsingParameterSupplier {

        @Theory
        public void theoryMethod(@ParametersSuppliedBy(SimpleSupplier.class) String param) {
        }

    }

    @Test
    public void shouldPickUpDataPointsFromParameterSupplier() throws Throwable {
        List<PotentialAssignment> assignments = potentialAssignments(TestClassUsingParameterSupplier.class
                .getMethod("theoryMethod", String.class));

        assertEquals(2, assignments.size());
        assertEquals(DATAPOINTS.get(0), assignments.get(0).getValue());
        assertEquals(DATAPOINTS.get(1), assignments.get(1).getValue());
    }
    
    public static class SupplierWithUnknownConstructor extends ParameterSupplier {
        
        public SupplierWithUnknownConstructor(String param) {
        }

        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            return null;
        }

    }

    @RunWith(Theories.class)
    public static class TestClassUsingSupplierWithUnknownConstructor {

        @Theory
        public void theory(@ParametersSuppliedBy(SupplierWithUnknownConstructor.class) String param) {
        }

    }
    
    @Test
    public void shouldRejectSuppliersWithUnknownConstructors() throws Exception {
        expected.expect(InitializationError.class);
        new Theories(TestClassUsingSupplierWithUnknownConstructor.class);
    }
    
    public static class SupplierWithTwoConstructors extends ParameterSupplier {
        
        public SupplierWithTwoConstructors(String param) {
        }

        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            return null;
        }

    }

    @RunWith(Theories.class)
    public static class TestClassUsingSupplierWithTwoConstructors {

        @Theory
        public void theory(@ParametersSuppliedBy(SupplierWithTwoConstructors.class) String param) {
        }

    }
    
    @Test
    public void shouldRejectSuppliersWithTwoConstructors() throws Exception {
        expected.expect(InitializationError.class);
        new Theories(TestClassUsingSupplierWithTwoConstructors.class);
    }
    
    public static class SupplierWithTestClassConstructor extends ParameterSupplier {
        
        public SupplierWithTestClassConstructor(TestClass param) {
        }

        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            return null;
        }

    }

    @RunWith(Theories.class)
    public static class TestClassUsingSupplierWithTestClassConstructor {

        @Theory
        public void theory(@ParametersSuppliedBy(SupplierWithTestClassConstructor.class) String param) {
        }

    }
    
    @Test
    public void shouldAcceptSuppliersWithTestClassConstructor() throws Exception {
        new Theories(TestClassUsingSupplierWithTestClassConstructor.class);
    }

}
