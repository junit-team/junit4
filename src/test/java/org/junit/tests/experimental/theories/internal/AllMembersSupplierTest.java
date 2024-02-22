package org.junit.tests.experimental.theories.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.tests.experimental.theories.TheoryTestUtils.potentialAssignments;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.internal.AllMembersSupplier;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.TestClass;

public class AllMembersSupplierTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();
    
    public static class HasDataPointsArrayField {
        @DataPoints
        public static String[] list = new String[] { "qwe", "asd" };

        @Theory
        public void theory(String param) {
        }
    }
    
    @Test
    public void dataPointsArrayShouldBeRecognized() throws Throwable {
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
    public void dataPointsArrayShouldBeRecognizedOnValueTypeNotFieldType() throws Throwable {
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
    public void dataPointMethodShouldBeRecognizedForOverlyGeneralParameters() throws Throwable {
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
    public void dataPointsAnnotationMeansTreatAsArrayOnly() throws Throwable {
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
    
    public static class HasDataPointsListField {
        @DataPoints
        public static List<String> list = Arrays.asList("one", "two");

        @Theory
        public void theory(String param) {
        }
    }

    @Test
    public void dataPointsCollectionFieldsShouldBeRecognized() throws Throwable {
        List<PotentialAssignment> assignments = potentialAssignments(
            HasDataPointsListField.class.getMethod("theory", String.class));

        assertEquals(2, assignments.size());
    }
    
    public static class HasDataPointsListMethod {
        @DataPoints
        public static List<String> getList() {
            return Arrays.asList("one", "two");
        }

        @Theory
        public void theory(String param) {
        }
    }

    @Test
    public void dataPointsCollectionMethodShouldBeRecognized() throws Throwable {
        List<PotentialAssignment> assignments = potentialAssignments(
            HasDataPointsListMethod.class.getMethod("theory", String.class));

        assertEquals(2, assignments.size());
    }
    
    public static class HasDataPointsListFieldWithOverlyGenericTypes {
        @DataPoints
        public static List<Object> list = Arrays.asList("string", new Object());

        @Theory
        public void theory(String param) {
        }
    }

    @Test
    public void dataPointsCollectionShouldBeRecognizedIgnoringStrangeTypes() throws Throwable {
        List<PotentialAssignment> assignments = potentialAssignments(
            HasDataPointsListFieldWithOverlyGenericTypes.class.getMethod("theory", String.class));

        assertEquals(1, assignments.size());
    }
}
