package org.junit.tests.experimental.theories.runner;

import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;

import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class WhenNoParametersMatchEnumeratedTypes {
    public enum SomeEnum {
      FIRST, SECOND
    }

    @RunWith(Theories.class)
    public static class AssumptionsFailBoolean {
        @Theory
        public void shouldFailBecauseExplicitFromDataPointsNotKnown(
                @FromDataPoints("unknown") boolean b) {}

        @Theory
        public void shouldSucceedBecauseNoExplicitFromDataPoints(boolean b) {}
    }

    @Test
    public void showFailedAssumptionsWhenNoParametersFoundBoolean() {
        assertThat(
                testResult(AssumptionsFailBoolean.class),
                hasSingleFailureContaining(
                        "Never found parameters that satisfied method assumptions"));
    }

    @RunWith(Theories.class)
    public static class AssumptionsFailEnum {
        @Theory
        public void shouldFailBecauseExplicitFromDataPointsNotKnown(
                @FromDataPoints("unknown") SomeEnum e) {}

        @Theory
        public void shouldSucceedBecauseNoExplicitFromDataPoints(SomeEnum e) {}
    }

    @Test
    public void showFailedAssumptionsWhenNoParametersFoundEnum() {
        assertThat(
                testResult(AssumptionsFailEnum.class),
                hasSingleFailureContaining(
                        "Never found parameters that satisfied method assumptions"));
    }

    @RunWith(Theories.class)
    public static class AssumptionsFailWrongType {
        @DataPoints("known") public static final String[] known = {"known"};

        @Theory
        public void shouldSucceedBecauseRightType(@FromDataPoints("known") String s) {}

        @Theory
        public void shouldFailBecauseWrongTypeBoolean(@FromDataPoints("known") boolean b) {}

        @Theory
        public void shouldFailBecauseWrongTypeEnum(@FromDataPoints("known") SomeEnum e) {}
    }

    @Test
    public void showFailedAssumptionsWhenWrongType() {
        PrintableResult result = testResult(AssumptionsFailWrongType.class);
        assertThat(result, failureCountIs(2));
        assertThat(
                result,
                hasFailureContaining(
                        "Never found parameters that satisfied method assumptions"));
    }
}
