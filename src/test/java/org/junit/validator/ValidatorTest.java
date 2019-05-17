package org.junit.validator;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InvalidTestClassError;

@RunWith(BlockJUnit4ClassRunner.class)
public class ValidatorTest {

    @Test
    public void testValidator() {
        Result result = JUnitCore.runClasses(ValidatorInnerTest.class);
        Assert.assertEquals(result.getFailureCount(), 1L);
        MatcherAssert.assertThat(result.getFailures().get(0).getException(),
                CoreMatchers.<Throwable>instanceOf(InvalidTestClassError.class));
    }

    @ValidateWith(ThrowExceptionValidator.class)
    public static class ValidatorInnerTest {
        @Test
        public void testValidator() {
            Assert.assertEquals(1L, 1L);
        }

    }


}
