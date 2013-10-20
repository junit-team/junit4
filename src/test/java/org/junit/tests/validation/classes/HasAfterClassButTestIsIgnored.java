package org.junit.tests.validation.classes;

import static org.junit.Assert.fail;
import static org.junit.tests.validation.ClassLevelMethodsOnlyRunWhenNecessaryTest.OUR_FAILURE_MSG;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

public class HasAfterClassButTestIsIgnored {

    @Ignore
    @Test
    public void test() throws Exception {
        fail("test() should not be run");
    }

    @AfterClass
    public static void afterClass() {
        fail(OUR_FAILURE_MSG);
    }
}
