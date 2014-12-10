package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.rules.TemporaryFolder;

public class TemporaryFolderRuleAssuredDeletionTest {
    
    public static class TemporaryFolderStub extends TemporaryFolder {
        public TemporaryFolderStub(BuilderStub builder) {
            super(builder);
        }

        /*
         * Don't need to create as we are overriding deletion
         */
        @Override
        public void create() throws IOException {
            
        }
        
        /*
         * Simulates failure to clean-up temporary folder
         */
        @Override
        protected boolean tryDelete() {
            return false;
        }
    }
    
    public static class BuilderStub extends TemporaryFolder.Builder {
        @Override
        public TemporaryFolder build() {
            return new TemporaryFolderStub(this);
        }
    }
    
    public static class HasTempFolderWithAssuredDeletion {
        @Rule public TemporaryFolder folder = new BuilderStub().assureDeletion().build();
        
        @Test
        public void test() {
            // no-op
        }
    }
    
    @Test
    public void testStrictVerificationFailure() {
        PrintableResult result = testResult(HasTempFolderWithAssuredDeletion.class);
        assertThat(result, failureCountIs(1));
        assertThat(result.toString(), CoreMatchers.containsString("Unable to clean up temporary folder"));
    }
    
    public static class HasTempFolderWithoutAssuredDeletion {
        @Rule public TemporaryFolder folder = new BuilderStub().build();
        
        @Test
        public void test() {
               // no-op
        }
    }
    
    @Test
    public void testStrictVerificationSuccess() {
        PrintableResult result = testResult(HasTempFolderWithoutAssuredDeletion.class);
        assertThat(result, isSuccessful());
    }
}
