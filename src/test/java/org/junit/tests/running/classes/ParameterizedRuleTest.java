package org.junit.tests.running.classes;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.UseParameterRule;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;

public class ParameterizedRuleTest {

    @Test
    public void testNoParameterRule() throws InitializationError {
        List<Runner> runners = new ParameterisedRunnerAccessorClass(NoParameterRuleTestClass.class).getChildren();
        assertEquals("Incorrect number of runners", 1, runners.size());
        assertEquals("Incorrect runner type", Parameterized.TestClassRunnerForParameters.class, runners.get(0).getClass());
    }

    public static class NoParameterRuleTestClass {

        @Parameterized.Parameter
        public int field;

        @Parameterized.Parameters
        public static Collection<Object[]> params() {
            return Arrays.asList(new Object[][]{new Object[]{1}});
        }

        @Test
        public void testNothing() {
            //Does nothing
        }

    }

    @Test
    public void testCustomParameterRule() throws InitializationError {
        List<Runner> runners = new ParameterisedRunnerAccessorClass(CustomParameterRuleTestClass.class).getChildren();
        assertEquals("Incorrect number of runners", 1, runners.size());
        assertEquals("Incorrect runner type", CustomParameterRunner.class, runners.get(0).getClass());
    }

    public static class CustomParameterRuleTestClass extends  NoParameterRuleTestClass {

        @UseParameterRule public static Parameterized.ParameterRule rule = new Parameterized.ParameterRule() {
            public Parameterized.ParameterRunnerBuilder apply(Parameterized.ParameterRunnerBuilder builder) {
                if (!(builder instanceof Parameterized.DefaultBuilder)) {
                    return builder;
                }
                return new Parameterized.ParameterRunnerBuilder() {
                    public Runner build(Class<?> type, String pattern, int index, Object[] parameters) throws InitializationError {
                        return new CustomParameterRunner(type);
                    }
                };
            }
        };


        @Parameterized.Parameter
        public int field;

        @Parameterized.Parameters
        public static Collection<Object[]> params() {
            return Arrays.asList(new Object[][]{new Object[]{1}});
        }

        @Test
        public void testNothing() {
            //Does nothing
        }

    }


    public static class CustomParameterRunner extends BlockJUnit4ClassRunner {

        public CustomParameterRunner(Class<?> klass) throws InitializationError {
            super(klass);
        }
    }


    @Test
    public void testCustomChainedParameterRule() throws InitializationError {
        List<Runner> runners = new ParameterisedRunnerAccessorClass(CustomChainedParameterRuleTestClass.class).getChildren();
        assertEquals("Incorrect number of runners", 1, runners.size());
        assertEquals("Incorrect runner type", CustomParameterRunner2.class, runners.get(0).getClass());
    }

    public static class CustomChainedParameterRuleTestClass extends CustomParameterRuleTestClass {


        @UseParameterRule public static Parameterized.ParameterRule rule2 = new Parameterized
                .ParameterRule() {
            public Parameterized.ParameterRunnerBuilder apply(final Parameterized.ParameterRunnerBuilder builder) {
                return new Parameterized.ParameterRunnerBuilder() {
                    public Runner build(Class<?> type, String pattern, int index, Object[] parameters) throws InitializationError {
                        return new CustomParameterRunner2(type);
                    }
                };
            }
        };

        @Parameterized.Parameter
        public int field;

        @Parameterized.Parameters
        public static Collection<Object[]> params() {
            return Arrays.asList(new Object[][]{new Object[]{1}});
        }

        @Test
        public void testNothing() {
            //Does nothing
        }

    }


    public static class CustomParameterRunner2 extends BlockJUnit4ClassRunner {

        public CustomParameterRunner2(Class<?> klass) throws InitializationError {
            super(klass);
        }

    }

    @Test
    public void testPrivateParameterRule() throws InitializationError {
        try {
            new ParameterisedRunnerAccessorClass(PrivateParameterRuleTestClass.class);
            fail("Initialization error should have been thrown due to non public ParamterRule");
        }  catch(InitializationError err) {
            assertEquals("Incorrect error thrown whilst creating parameter rule",
                    "UseParameterRule annotated field 'rule' must be public",
                    err.getCauses().get(0).getMessage());
        }
    }

    public static class PrivateParameterRuleTestClass extends CustomParameterRuleTestClass {


        @UseParameterRule private Parameterized.ParameterRule rule = new Parameterized.ParameterRule() {
            public Parameterized.ParameterRunnerBuilder apply(final Parameterized.ParameterRunnerBuilder builder) {
                return new Parameterized.ParameterRunnerBuilder() {
                    public Runner build(Class<?> type, String pattern, int index, Object[] parameters) throws InitializationError {
                        return new CustomParameterRunner2(type);
                    }
                };
            }
        };

        @Parameterized.Parameter
        public int field;

        @Parameterized.Parameters
        public static Collection<Object[]> params() {
            return Arrays.asList(new Object[][]{new Object[]{1}});
        }

        @Test
        public void testNothing() {
            //Does nothing
        }

    }


    /**
     * This class purely acts as a facade to the real Parameterized class, but makes the methods we need to call accessible
     * to us since we're in a different package. If this test ever gets moved into the same package as Paramaterized then
     * we can dump this facade and call Parameterized directly
     */
    private static class ParameterisedRunnerAccessorClass extends Parameterized {

        public ParameterisedRunnerAccessorClass(Class<?> klass) throws InitializationError {
                super(klass);
        }

        public List<Runner> getChildren() {
            return super.getChildren();
        }
    }


    public static class ParameterRuleChainTest {
        private static final List<String> LOG = new ArrayList<String>();

        private static class LoggingRule implements Parameterized.ParameterRule {
            private final String message;

            public LoggingRule(String message) {
                this.message = message;
            }

            public Parameterized.ParameterRunnerBuilder apply(Parameterized.ParameterRunnerBuilder builder) {
                LOG.add(message);
                return builder;
            }
        }

        @RunWith(Parameterized.class)
        public static class UseParameterRuleChain {

            @Parameterized.Parameter(0) public int param1;

            @UseParameterRule
            public static final Parameterized.ParameterRuleChain chain = Parameterized
                    .ParameterRuleChain.outerRule(new LoggingRule("outer rule"))
                    .around(new LoggingRule("middle rule")).around(
                            new LoggingRule("inner rule"));

            @Parameterized.Parameters
            public static Collection<Object[]> parameters() {
                return Arrays.asList(new Object[][]{new Object[]{1}});
            }

            @Test
            public void example() {
                assertTrue(true);
            }
        }

    }

    @Test
    public void executeRulesInCorrectOrder() throws Exception {
        testResult(ParameterRuleChainTest.UseParameterRuleChain.class).toString();
        List<String> expectedLog = asList("inner rule", "middle rule",
                "outer rule");
        Assert.assertEquals(expectedLog, ParameterRuleChainTest.LOG);
    }
}
