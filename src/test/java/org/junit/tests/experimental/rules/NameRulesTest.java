package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class NameRulesTest {
    public static class TestNames {
        @Rule
        public TestName name = new TestName();

        @Test
        public void testA() {
            assertEquals("testA", name.getMethodName());
        }

        @Test
        public void testB() {
            assertEquals("testB", name.getMethodName());
        }
    }

    public static class BeforeAndAfterTest {
        @Rule
        public TestName name = new TestName();

        private final String expectedName = "x";

        @Before
        public void setUp() {
            assertEquals(expectedName, name.getMethodName());
        }

        @Test
        public void x() {
            assertEquals(expectedName, name.getMethodName());
        }

        @After
        public void tearDown() {
            assertEquals(expectedName, name.getMethodName());
        }
    }
}
