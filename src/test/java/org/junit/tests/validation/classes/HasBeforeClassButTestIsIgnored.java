package org.junit.tests.validation.classes;

import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.tests.validation.ClassLevelMethodsOnlyRunWhenNecessaryTest;

public class HasBeforeClassButTestIsIgnored {

    @BeforeClass
    public static void setUpClass() {
        fail(ClassLevelMethodsOnlyRunWhenNecessaryTest.OUR_FAILURE_MSG);
    }

    @Ignore
    @Test
    public void test() throws Exception {
        fail("test() should not be run");
    }

}
