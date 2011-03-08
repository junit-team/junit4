package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.junit.experimental.results.PrintableResult.*;
import static org.junit.experimental.results.ResultMatchers.*;
import static org.junit.experimental.theories.Reflector.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.ParameterMatching;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

public class GenericsMatching {
    @Test
    public void shouldMatchMismatchedParameterizedListsWhenGenericsAreOutOfPlay() {
        assertThat(testResult(NoGenericParameterMatching.class), isSuccessful());
    }

    @RunWith(Theories.class)
    public static class NoGenericParameterMatching {
        @DataPoint
        public static List<String> ITEMS = Arrays.asList("a", "b");

        @Theory
        public void matching(List<Integer> items) {
            assumeThat(items.size(), is(2));
            try {
                int first = items.get(0);
                fail("Should have failed cast because we smuggled in a List with non-integers");
            } catch (ClassCastException expected) {
                    // ignored
            }
        }
    }

    @Test
    public void shouldNotMatchMismatchedParameterizedListsWhenGenericsAreInPlay() {
        assertThat(testResult(GenericParameterMatching.class),
                hasSingleFailureContaining("Never found parameters that satisfied method assumptions"));
    }

    @RunWith(Theories.class)
    public static class GenericParameterMatching {
        @DataPoint
        public static List<String> ITEMS = Arrays.asList("a", "b");

        @Theory
        @ParameterMatching(WITH_GENERICS)
        public void matching(List<Integer> items) {
        }
    }

    @Test
    public void methodMatchingSettingShouldTrumpClassSetting() {
        assertThat(testResult(MethodTrumpsClass.class), isSuccessful());
    }

    @RunWith(Theories.class)
    @ParameterMatching(WITH_GENERICS)
    public static class MethodTrumpsClass {
        @DataPoint
        public static List<String> ITEMS = Arrays.asList("a", "b");

        @Theory
        @ParameterMatching(WITHOUT_GENERICS)
        public void matching(List<Integer> items) {
            assumeThat(items.size(), is(2));
            try {
                int first = items.get(0);
                fail("Should have failed cast because we smuggled in a List with non-integers");
            } catch (ClassCastException expected) {
                    // ignored
            }
        }
    }
}
