package org.junit.tests.validation.classes;

import static org.junit.Assert.fail;
import org.junit.BeforeClass;

public class HasBeforeClassButNoTests {
    @BeforeClass
    public static void setUpClass() {
        fail("setUpClass() should not be run");
    }
}
