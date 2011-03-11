package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class MatchingGenericParameters {
    @DataPoint public static List<String> strings = Arrays.asList("what");
    @DataPoint public static List<Integer> ints = Arrays.asList(1);

    @Theory
    public void regex(List<String> strings, List<Integer> ints) {
        assertThat(strings.get(0), is(String.class));
        assertThat(ints.get(0), is(Integer.class));
    }
}
