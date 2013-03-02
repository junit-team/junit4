package org.junit.tests.running.classes;

import org.junit.Test;
import org.junit.internal.builders.ParameterRunnerBuilder;
import org.junit.rules.ParameterRule;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.UseParameterRule;
import org.junit.runners.model.InitializationError;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

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

        @UseParameterRule public ParameterRule rule = new ParameterRule() {
            public ParameterRunnerBuilder apply(ParameterRunnerBuilder builder) {
                if (!(builder instanceof Parameterized.DefaultBuilder)) {
                    return builder;
                }
                return new ParameterRunnerBuilder() {
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


        @UseParameterRule public ParameterRule rule2 = new ParameterRule() {
            public ParameterRunnerBuilder apply(final ParameterRunnerBuilder builder) {
                return new ParameterRunnerBuilder() {
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
            assertEquals("Incorrect error thrown whilst creating parameter rule", "ParameterRules must be public", err.getCauses().get(0).getMessage());
        }
    }

    public static class PrivateParameterRuleTestClass extends CustomParameterRuleTestClass {


        @UseParameterRule private ParameterRule rule = new ParameterRule() {
            public ParameterRunnerBuilder apply(final ParameterRunnerBuilder builder) {
                return new ParameterRunnerBuilder() {
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


}
