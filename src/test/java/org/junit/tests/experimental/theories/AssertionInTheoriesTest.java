package org.junit.tests.experimental.theories;

import static org.junit.tests.experimental.theories.TheoryTestUtils.runTheoryClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theory;
import org.junit.runner.Result;
import org.junit.runners.model.InitializationError;

public class AssertionInTheoriesTest {

    @Test
    public void theoryMeansOnlyAssumeShouldFail() throws InitializationError {
        Result result = runTheoryClass(TheoryWithAssertionFailed.class);
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertEquals(result.getFailures().get(0).getException().getClass(), AssertionError.class);
    }

    public static class TheoryWithAssertionFailed {

        @DataPoint public final static boolean FALSE = false;

        @Theory
        public void theoryWithAssertionFailed(boolean value) {
        	Assert.assertTrue(value);
        }
    }

}
