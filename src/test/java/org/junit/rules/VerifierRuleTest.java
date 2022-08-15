package org.junit.rules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import org.junit.Rule;
import org.junit.Test;

public class VerifierRuleTest {

    private static String sequence;

    public static class UsesVerifier {
        @Rule
        public Verifier collector = new Verifier() {
            @Override
            protected void verify() {
                sequence += "verify ";
            }
        };

        @Test
        public void example() {
            sequence += "test ";
        }
    }

    @Test
    public void verifierRunsAfterTest() {
        sequence = "";
        assertThat(testResult(UsesVerifier.class), isSuccessful());
        assertEquals("test verify ", sequence);
    }
}
