package org.junit.tests.validation.classes;

import static org.junit.Assert.fail;
import static org.junit.tests.validation.ClassLevelMethodsOnlyRunWhenNecessaryTest.OUR_FAILURE_MSG;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class BeforeClassAndTestFailButClassIsIgnored {

    @BeforeClass
    public static void beforeClass() {
        fail(OUR_FAILURE_MSG);
    }

    @Test
    public void test() throws Exception {
        fail("test() should not be run");
    }

}
