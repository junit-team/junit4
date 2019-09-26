package org.junit.rules;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;

public class TemporaryFolderRuleAssuredDeletionTest {

    public static class TestClass {
        static TemporaryFolder injectedRule;

        @Rule
        public TemporaryFolder folder = injectedRule;

        @Test
        public void alwaysPassesButDeletesRootFolder() {
            //we delete the folder in the test so that it cannot be deleted by
            //the rule
            folder.getRoot().delete();
        }
    }

    @Test
    public void testFailsWhenCreatedFolderCannotBeDeletedButDeletionIsAssured() {
        TestClass.injectedRule = TemporaryFolder.builder()
                .assureDeletion()
                .build();
        PrintableResult result = testResult(TestClass.class);
        assertThat(result, failureCountIs(1));
        assertThat(result.toString(), containsString("Unable to clean up temporary folder"));
    }

    @Test
    public void byDefaultTestDoesNotFailWhenCreatedFolderCannotBeDeleted() {
        TestClass.injectedRule = new TemporaryFolder();
        PrintableResult result = testResult(TestClass.class);
        assertThat(result, isSuccessful());
    }
}
