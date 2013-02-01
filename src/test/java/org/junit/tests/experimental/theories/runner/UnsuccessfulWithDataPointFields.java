package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.model.TestClass;

public class UnsuccessfulWithDataPointFields {
    @RunWith(Theories.class)
    public static class HasAFailingTheory {
        @DataPoint
        public static int ONE = 1;

        @Theory
        public void everythingIsZero(int x) {
            assertThat(x, is(0));
        }
    }

    @Test
    public void theoryClassMethodsShowUp() throws Exception {
        assertThat(new Theories(HasAFailingTheory.class).getDescription()
                .getChildren().size(), is(1));
    }

    @Test
    public void theoryAnnotationsAreRetained() throws Exception {
        assertThat(new TestClass(HasAFailingTheory.class).getAnnotatedMethods(
                Theory.class).size(), is(1));
    }

    @Test
    public void canRunTheories() throws Exception {
        assertThat(testResult(HasAFailingTheory.class),
                hasSingleFailureContaining("Expected"));
    }

    @RunWith(Theories.class)
    public static class DoesntUseParams {
        @DataPoint
        public static int ONE = 1;

        @Theory
        public void everythingIsZero(int x, int y) {
            assertThat(2, is(3));
        }
    }

    @Test
    public void reportBadParams() throws Exception {
        assertThat(testResult(DoesntUseParams.class),
                hasSingleFailureContaining("everythingIsZero(\"1\" <from ONE>, \"1\" <from ONE>)"));
    }

    @RunWith(Theories.class)
    public static class NullsOK {
        @DataPoint
        public static String NULL = null;

        @DataPoint
        public static String A = "A";

        @Theory
        public void everythingIsA(String a) {
            assertThat(a, is("A"));
        }
    }

    @Test
    public void nullsUsedUnlessProhibited() throws Exception {
        assertThat(testResult(NullsOK.class),
                hasSingleFailureContaining("null"));
    }
    
    @RunWith(Theories.class)
    public static class TheoriesMustBePublic {
        @DataPoint
        public static int THREE = 3;

        @Theory
        void numbers(int x) {

        }
    }

    @Test
    public void theoriesMustBePublic() {
        assertThat(
                testResult(TheoriesMustBePublic.class),
                hasSingleFailureContaining("public"));
    }    

    @RunWith(Theories.class)
    public static class DataPointFieldsMustBeStatic {
        @DataPoint
        public int THREE = 3;
        
        @DataPoints
        public int[] FOURS = new int[] { 4 };
        
        @Theory
        public void numbers(int x) {

        }
    }

    @Test
    public void dataPointFieldsMustBeStatic() {
        assertThat(
                testResult(DataPointFieldsMustBeStatic.class),
                CoreMatchers.<PrintableResult>both(failureCountIs(2))
                        .and(
                                hasFailureContaining("DataPoint field THREE must be static"))
                        .and(
                                hasFailureContaining("DataPoint field FOURS must be static")));
    }
    
    @RunWith(Theories.class)
    public static class DataPointMethodsMustBeStatic {
        @DataPoint
        public int singleDataPointMethod() {
            return 1;
        }
        
        @DataPoints
        public int[] dataPointArrayMethod() {
            return new int[] { 1, 2, 3 };
        }

        @Theory
        public void numbers(int x) {
            
        }
    }
    
    @Test
    public void dataPointMethodsMustBeStatic() {
        assertThat(
                testResult(DataPointMethodsMustBeStatic.class),
                CoreMatchers.<PrintableResult>both(failureCountIs(2))
                .and(
                        hasFailureContaining("DataPoint method singleDataPointMethod must be static"))
                .and(
                        hasFailureContaining("DataPoint method dataPointArrayMethod must be static")));
    }

    @RunWith(Theories.class)
    public static class DataPointFieldsMustBePublic {
        @DataPoint
        static int THREE = 3;
        
        @DataPoints
        static int[] THREES = new int[] { 3 };

        @DataPoint
        protected static int FOUR = 4;
        
        @DataPoints
        protected static int[] FOURS = new int[] { 4 };

        @DataPoint
        private static int FIVE = 5;
        
        @DataPoints
        private static int[] FIVES = new int[] { 5 };

        @Theory
        public void numbers(int x) {
        	
        }
    }

    @Test
    public void dataPointFieldsMustBePublic() {
        PrintableResult result = testResult(DataPointFieldsMustBePublic.class);        
        assertEquals(6, result.failureCount());

        assertThat(result,
                allOf(hasFailureContaining("DataPoint field THREE must be public"),
                      hasFailureContaining("DataPoint field THREES must be public"),
                      hasFailureContaining("DataPoint field FOUR must be public"),
                      hasFailureContaining("DataPoint field FOURS must be public"),
                      hasFailureContaining("DataPoint field FIVE must be public"),
                      hasFailureContaining("DataPoint field FIVES must be public")));
    }

    @RunWith(Theories.class)
    public static class DataPointMethodsMustBePublic {
        @DataPoint
        static int three() {
            return 3;
        }
        
        @DataPoints
        static int[] threes() { 
            return new int[] { 3 };
        }

        @DataPoint
        protected static int four() {
            return 4;
        }
        
        @DataPoints
        protected static int[] fours() {
            return new int[] { 4 };
        }

        @DataPoint
        private static int five() {
            return 5;
        }
        
        @DataPoints
        private static int[] fives() {
            return new int[] { 5 };
        }

        @Theory
        public void numbers(int x) {
        	
        }
    }
    
    @Test
    public void dataPointMethodsMustBePublic() {
        PrintableResult result = testResult(DataPointMethodsMustBePublic.class);        
        assertEquals(6, result.failureCount());

        assertThat(result,
                allOf(hasFailureContaining("DataPoint method three must be public"),
                      hasFailureContaining("DataPoint method threes must be public"),
                      hasFailureContaining("DataPoint method four must be public"),
                      hasFailureContaining("DataPoint method fours must be public"),
                      hasFailureContaining("DataPoint method five must be public"),
                      hasFailureContaining("DataPoint method fives must be public")));
    }
}
