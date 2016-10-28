package org.junit.rules;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

public class ExternalResourceRuleTest {
    private static String callSequence;

    public static class UsesExternalResource {
        @Rule
        public ExternalResource resource = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                callSequence += "before ";
            }

            ;

            @Override
            protected void after() {
                callSequence += "after ";
            }

            ;
        };

        @Test
        public void testFoo() {
            callSequence += "test ";
        }
    }

    @Test
    public void externalResourceGeneratesCorrectSequence() {
        callSequence = "";
        assertThat(testResult(UsesExternalResource.class), isSuccessful());
        assertEquals("before test after ", callSequence);
    }

    @Test
    public void shouldThrowMultipleFailureExceptionWhenTestFailsAndClosingResourceFails() throws Throwable {
        // given
        ExternalResource resourceRule = new ExternalResource() {
            @Override
            protected void after() {
                throw new RuntimeException("simulating resource tear down failure");
            }
        };
        Statement failingTest = new Fail(new RuntimeException("simulated test failure"));
        Description dummyDescription = Description.createTestDescription(
                "dummy test class name", "dummy test name");

        try {
            resourceRule.apply(failingTest, dummyDescription).evaluate();
            fail("ExternalResource should throw");
        } catch (MultipleFailureException e) {
            assertThat(e.getMessage(), allOf(
                    containsString("simulated test failure"),
                    containsString("simulating resource tear down failure")
            ));
        }
    }
}
