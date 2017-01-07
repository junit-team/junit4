package org.junit.tests.validation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

public class ValidationTest {
    public static class WrongBeforeClass {
        @BeforeClass
        protected int a() {
            return 0;
        }
    }

    @Test
    public void initializationErrorIsOnCorrectClass() {
        assertEquals(WrongBeforeClass.class.getName(),
                Request.aClass(WrongBeforeClass.class).getRunner().getDescription().getDisplayName());
    }

    public static class NonStaticBeforeClass {
        @BeforeClass
        public void before() {
        }

        @Test
        public void hereBecauseEveryTestClassNeedsATest() {
        }
    }

    @Test
    public void nonStaticBeforeClass() {
        Result result = JUnitCore.runClasses(NonStaticBeforeClass.class);
        assertThat(result.getFailures().get(0).getMessage(), containsString("Method before() should be static"));
    }
}
