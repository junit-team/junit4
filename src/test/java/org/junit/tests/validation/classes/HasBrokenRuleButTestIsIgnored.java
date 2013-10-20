package org.junit.tests.validation.classes;

import static org.junit.Assert.fail;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.tests.validation.ClassLevelMethodsOnlyRunWhenNecessaryTest.BrokenRule;

public class HasBrokenRuleButTestIsIgnored {

    @ClassRule
    public static BrokenRule brokenRule = new BrokenRule();
    
    @Ignore
    @Test
    public void test() throws Exception {
        fail("test() should not be run");
    }

}
