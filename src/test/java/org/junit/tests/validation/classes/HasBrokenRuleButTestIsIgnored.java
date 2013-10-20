package org.junit.tests.validation.classes;

import static org.junit.Assert.fail;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class HasBrokenRuleButTestIsIgnored {
    
    public static class BrokenRule implements TestRule {
        public Statement apply(Statement base, Description description) {
            throw new RuntimeException("this rule is broken");
        }
    }

    @ClassRule
    public static BrokenRule brokenRule = new BrokenRule();
    
    @Ignore
    @Test
    public void test() throws Exception {
        fail("test() should not be run");
    }

}
