package org.junit.tests.experimental.theories;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Reflector;
import org.junit.experimental.theories.internal.AllMembersSupplier;
import org.junit.runners.model.TestClass;

public class AllMembersSupplierTest {
    public static class HasDataPoints {
        @DataPoints
        public static Object[] objects= { 1, 2 };

        public HasDataPoints(Object obj) {
        }
    }

    @Test
    public void dataPointsAnnotationMeansTreatAsArrayOnly()
            throws SecurityException, NoSuchMethodException {
        List<PotentialAssignment> valueSources= new AllMembersSupplier(
                new TestClass(HasDataPoints.class), Reflector.WITHOUT_GENERICS)
                .getValueSources(ParameterSignature.signatures(
                        HasDataPoints.class.getConstructor(Object.class),
                        Reflector.WITHOUT_GENERICS)
                        .get(0));
        assertThat(valueSources.size(), is(2));
    }
}
