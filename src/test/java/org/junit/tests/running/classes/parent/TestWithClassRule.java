package org.junit.tests.running.classes.parent;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Field;

/**
 * Test class for validate run tests that was load in own ClassLoader
 */
public class TestWithClassRule {
    public static Class<?> applyTestClass;

    @ClassRule
    public static TestRule rule = new CustomRule();

    @Test
    public void testClassRuleExecuted() throws Exception {
        Assert.assertNotNull("Description should contain reference to TestClass", applyTestClass);
    }

    public static final class CustomRule implements TestRule {

        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    Class<?> testClass = description.getTestClass();
                    if(testClass != null) {
                        Field field = testClass.getDeclaredField("applyTestClass");
                        field.set(null, description.getTestClass());
                    }
                    base.evaluate();
                }
            };
        }
    }
}
