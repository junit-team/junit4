package org.junit.tests.experimental.theories.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.internal.AllMembersSupplier;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.TestClass;

public class AllMembersSupplierTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();
    
    public static class HasDataPoints {
        @DataPoints
        public static Object[] objects = {1, 2};

        public HasDataPoints(Object obj) {
        }
    }

    @Test
    public void dataPointsAnnotationMeansTreatAsArrayOnly() throws Throwable {
        List<PotentialAssignment> valueSources = allMemberValuesFor(
                HasDataPoints.class, Object.class);
        assertThat(valueSources.size(), is(2));
    }

    public static class HasDataPointsFieldWithNullValue {
        @DataPoints
        public static Object[] objects = {null, "a"};

        public HasDataPointsFieldWithNullValue(Object obj) {
        }
    }

    @Test
    public void dataPointsArrayFieldMayContainNullValue() throws Throwable {
        List<PotentialAssignment> valueSources = allMemberValuesFor(
                HasDataPointsFieldWithNullValue.class, Object.class);
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
    public void dataPointsArrayMethodMayContainNullValue() throws Throwable {
        List<PotentialAssignment> valueSources = allMemberValuesFor(
                HasDataPointsMethodWithNullValue.class, Integer.class);
        assertThat(valueSources.size(), is(2));
    }
    
    public static class HasFailingDataPointsArrayMethod {
        @DataPoints
        public static Object[] objects() {
            throw new RuntimeException("failing method");
        }

        public HasFailingDataPointsArrayMethod(Object obj) {
        }
    }

    @Test
    public void allMembersFailsOnFailingDataPointsArrayMethod() throws Throwable {
        expected.expect(RuntimeException.class);
        expected.expectMessage("failing method");
        allMemberValuesFor(HasFailingDataPointsArrayMethod.class, Object.class);
    }

    private List<PotentialAssignment> allMemberValuesFor(Class<?> testClass,
            Class<?>... constructorParameterTypes) throws Throwable {
        return new AllMembersSupplier(new TestClass(testClass))
                .getValueSources(ParameterSignature.signatures(
                        testClass.getConstructor(constructorParameterTypes))
                        .get(0));
    }
}
