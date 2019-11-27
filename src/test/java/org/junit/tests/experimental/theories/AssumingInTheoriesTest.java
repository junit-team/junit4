package org.junit.tests.experimental.theories;

import static org.junit.tests.experimental.theories.TheoryTestUtils.runTheoryClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

@RunWith(Theories.class)
public class AssumingInTheoriesTest {

    @Test
    public void noTheoryAnnotationMeansAssumeShouldIgnore() {
        Assume.assumeTrue(false);
    }

    @Test
    public void theoryMeansOnlyAssumeShouldFail() throws InitializationError {
        Result result = runTheoryClass(TheoryWithNoUnassumedParameters.class);
        Assert.assertEquals(1, result.getFailureCount());
    }

    /**
     * Simple class that SHOULD fail because no parameters are met.
     */
    public static class TheoryWithNoUnassumedParameters {

        @DataPoint
        public static final boolean FALSE = false;

        @Theory
        public void theoryWithNoUnassumedParameters(boolean value) {
            Assume.assumeTrue(value);
        }
    }

}
