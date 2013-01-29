package org.junit.tests.experimental.theories.runner;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.runner.RunWith;
import org.junit.runners.model.TestClass;

public class WithParameterSupplier {

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

    public static class Supplier extends ParameterSupplier {

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
        public void theoryMethod(@ParametersSuppliedBy(Supplier.class)
        String parameter) {
        }

    }

    @Test
    public void shouldPickUpDataPointsFromParameterSupplier() throws Exception {
        List<PotentialAssignment> assignments = potentialValues(TestClassUsingParameterSupplier.class
                .getMethod("theoryMethod", String.class));

        assertEquals(2, assignments.size());
        assertEquals(DATAPOINTS.get(0), assignments.get(0).getValue());
        assertEquals(DATAPOINTS.get(1), assignments.get(1).getValue());
    }

    private List<PotentialAssignment> potentialValues(Method method)
            throws Exception {
        return Assignments.allUnassigned(method,
                new TestClass(method.getDeclaringClass()))
                .potentialsForNextUnassigned();
    }

}
