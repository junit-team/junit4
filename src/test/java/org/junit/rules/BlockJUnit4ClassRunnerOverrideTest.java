package org.junit.rules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class BlockJUnit4ClassRunnerOverrideTest {
    public static class FlipBitRule implements MethodRule {
        public Statement apply(final Statement base, FrameworkMethod method,
                final Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    target.getClass().getField("flipBit").set(target, true);
                    base.evaluate();
                }
            };
        }

    }

    public static class OverrideRulesRunner extends BlockJUnit4ClassRunner {
        public OverrideRulesRunner(Class<?> klass) throws InitializationError {
            super(klass);
        }

        @Override
        protected List<MethodRule> rules(Object test) {
            final LinkedList<MethodRule> methodRules = new LinkedList<MethodRule>(
                    super.rules(test));
            methodRules.add(new FlipBitRule());
            return methodRules;
        }
    }

    @RunWith(OverrideRulesRunner.class)
    public static class OverrideRulesTest {
        public boolean flipBit = false;

        @Test
        public void testFlipBit() {
            assertTrue(flipBit);
        }
    }

    @Test
    public void overrideRulesMethod() {
        assertThat(testResult(OverrideTestRulesTest.class), isSuccessful());
    }

    public static class OverrideTestRulesRunner extends BlockJUnit4ClassRunner {
        public OverrideTestRulesRunner(Class<?> klass)
                throws InitializationError {
            super(klass);
        }

        @Override
        protected List<TestRule> getTestRules(final Object test) {
            final LinkedList<TestRule> methodRules = new LinkedList<TestRule>(
                    super.getTestRules(test));
            methodRules.add(new TestRule() {
                public Statement apply(Statement base, Description description) {
                    return new FlipBitRule().apply(base, null, test);
                }
            });
            return methodRules;
        }
    }

    @RunWith(OverrideTestRulesRunner.class)
    public static class OverrideTestRulesTest extends OverrideRulesTest {
    }

    @Test
    public void overrideTestRulesMethod() {
        assertThat(testResult(OverrideRulesTest.class), isSuccessful());
    }


    /**
     * Runner for testing override of {@link org.junit.runners.BlockJUnit4ClassRunner#createTest(org.junit.runners.model.FrameworkMethod)}
     * by setting the {@link org.junit.runners.model.FrameworkMethod} in a field
     * of the test class so it can be compared with the test method that is being
     * executed.
     */
    public static class OverrideCreateTestRunner extends BlockJUnit4ClassRunner {
        public OverrideCreateTestRunner(final Class<?> klass) throws InitializationError {
            super(klass);

            assert(klass.equals(OverrideCreateTest.class));
        }

        @Override
        protected Object createTest(FrameworkMethod method) {
            final OverrideCreateTest obj = new OverrideCreateTest();

            obj.method = method;

            return obj;
        }
    }

    @RunWith(OverrideCreateTestRunner.class)
    public static class OverrideCreateTest {
        public FrameworkMethod method;

        @Test
        public void testMethodA() {
            assertEquals("testMethodA", method.getMethod().getName());
        }

        @Test
        public void testMethodB() {
            assertEquals("testMethodB", method.getMethod().getName());
        }
    }

    @Test
    public void overrideCreateTestMethod() {
        assertThat(testResult(OverrideCreateTest.class), isSuccessful());
    }


    /**
     * Runner for testing override of {@link org.junit.runners.BlockJUnit4ClassRunner#createTest()}
     * is still called by default if no other {@code createTest} method override
     * is in place. This is tested by setting a boolean flag in a field of the
     * test class so it can be checked to confirm that the createTest method was
     * called.
     */
    public static class CreateTestDefersToNoArgCreateTestRunner extends BlockJUnit4ClassRunner {
        public CreateTestDefersToNoArgCreateTestRunner(final Class<?> klass) throws InitializationError {
            super(klass);

            assert(klass.equals(CreateTestDefersToNoArgCreateTestTest.class));
        }

        @Override
        protected Object createTest() {
            final CreateTestDefersToNoArgCreateTestTest obj = new CreateTestDefersToNoArgCreateTestTest();

            obj.createTestCalled = true;

            return obj;
        }
    }

    @RunWith(CreateTestDefersToNoArgCreateTestRunner.class)
    public static class CreateTestDefersToNoArgCreateTestTest {
        public boolean createTestCalled = false;

        @Test
        public void testCreateTestCalled() {
            assertEquals(true, createTestCalled);
        }
    }

    @Test
    public void createTestDefersToNoArgCreateTest() {
        assertThat(testResult(CreateTestDefersToNoArgCreateTestTest.class), isSuccessful());
    }
}
