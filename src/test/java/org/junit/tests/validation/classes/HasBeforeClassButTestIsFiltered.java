package org.junit.tests.validation.classes;

import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.tests.validation.ClassLevelMethodsOnlyRunWhenNecessaryTest;

public class HasBeforeClassButTestIsFiltered {
    
    public interface FilteredTests {
    }

    @BeforeClass
    public static void setUpClass() {
        fail(ClassLevelMethodsOnlyRunWhenNecessaryTest.OUR_FAILURE_MSG);
    }

    @Category(FilteredTests.class)
    @Test
    public void test() throws Exception {
        fail("test() should not be run");
    }
}
