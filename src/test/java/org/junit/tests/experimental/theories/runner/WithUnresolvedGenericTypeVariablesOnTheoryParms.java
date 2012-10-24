package org.junit.tests.experimental.theories.runner;

import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

public class WithUnresolvedGenericTypeVariablesOnTheoryParms {
    @Test
    public void whereTypeVariableIsOnTheTheory() {
        PrintableResult result = testResult(TypeVariableOnTheoryOnly.class);
        assertThat(result, isSuccessful());
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnTheoryOnly {
        @DataPoint
        public static List<String> strings = Arrays.asList("foo", "bar");

        @Theory
        public <T> void forItems(Collection<?> items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnTheoryParm() {
        PrintableResult result = testResult(TypeVariableOnTheoryParm.class);
        assertThat(result, hasSingleFailureContaining("unresolved type variable T"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnTheoryParm {
        @DataPoint
        public static String string = "foo";

        @Theory
        public <T> void forItem(T item) {
        }
    }

    @Test
    public void whereTypeVariableIsOnParameterizedTheoryParm() {
        PrintableResult result = testResult(TypeVariableOnParameterizedTheoryParm.class);
        assertThat(result, hasSingleFailureContaining("unresolved type variable T"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnParameterizedTheoryParm {
        @DataPoint
        public static List<String> strings = Arrays.asList("foo", "bar");

        @Theory
        public <T> void forItems(Collection<T> items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnWildcardUpperBoundOnTheoryParm() {
        PrintableResult result = testResult(TypeVariableOnWildcardUpperBoundOnTheoryParm.class);
        assertThat(result, hasSingleFailureContaining("unresolved type variable U"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnWildcardUpperBoundOnTheoryParm {
        @DataPoint
        public static List<String> strings = Arrays.asList("foo", "bar");

        @Theory
        public <U> void forItems(Collection<? extends U> items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnWildcardLowerBoundOnTheoryParm() {
        PrintableResult result = testResult(TypeVariableOnWildcardLowerBoundOnTheoryParm.class);
        assertThat(result, hasSingleFailureContaining("unresolved type variable V"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnWildcardLowerBoundOnTheoryParm {
        @DataPoint
        public static List<String> strings = Arrays.asList("foo", "bar");

        @Theory
        public <V> void forItems(Collection<? super V> items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnArrayTypeOnTheoryParm() {
        PrintableResult result = testResult(TypeVariableOnArrayTypeOnTheoryParm.class);
        assertThat(result, hasSingleFailureContaining("unresolved type variable T"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnArrayTypeOnTheoryParm {
        @DataPoints
        public static String[][] items() {
            return new String[][]{new String[]{"foo"}, new String[]{"bar"}};
        }

        @Theory
        public <T> void forItems(T[] items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnComponentOfArrayTypeOnTheoryParm() {
        PrintableResult result = testResult(TypeVariableOnComponentOfArrayTypeOnTheoryParm.class);
        assertThat(result, hasSingleFailureContaining("unresolved type variable U"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnComponentOfArrayTypeOnTheoryParm {
        @DataPoints
        public static List<?>[][] items() {
            return new List<?>[][]{
                    new List<?>[]{Arrays.asList("foo")},
                    new List<?>[]{Arrays.asList("bar")}
            };
        }

        @Theory
        public <U> void forItems(Collection<U>[] items) {
        }
    }

    @Test
    public void whereTypeVariableIsOnTheoryClass() {
        PrintableResult result = testResult(TypeVariableOnTheoryClass.class);
        assertThat(result, hasSingleFailureContaining("unresolved type variable T"));
    }

    @RunWith(Theories.class)
    public static class TypeVariableOnTheoryClass<T> {
        @DataPoint
        public static String item = "bar";

        @Theory
        public void forItem(T item) {
        }
    }

    @Test
    public void whereTypeVariablesAbound() {
        PrintableResult result = testResult(TypeVariablesAbound.class);
        assertThat(result, failureCountIs(7));
        assertThat(result, hasFailureContaining("unresolved type variable A"));
        assertThat(result, hasFailureContaining("unresolved type variable B"));
        assertThat(result, hasFailureContaining("unresolved type variable C"));
        assertThat(result, hasFailureContaining("unresolved type variable D"));
        assertThat(result, hasFailureContaining("unresolved type variable E"));
        assertThat(result, hasFailureContaining("unresolved type variable F"));
        assertThat(result, hasFailureContaining("unresolved type variable G"));
    }

    @RunWith(Theories.class)
    public static class TypeVariablesAbound<A, B extends A, C extends Collection<B>> {
        @Theory
        public <D, E extends D, F, G> void forItem(A first, Collection<B> second,
                Map<C, ? extends D> third, List<? super E> fourth, F[] fifth,
                Collection<G>[] sixth) {
        }
    }
}