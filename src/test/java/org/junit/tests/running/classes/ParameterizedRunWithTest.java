package org.junit.tests.running.classes;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
public class ParameterizedRunWithTest {

    @Test
    public void countWithParameterizedField() {
        Result result = JUnitCore.runClasses(ExtendedTestClassRunnerForParametersTest.class);
        assertEquals("Test not run correct number of times", 5, result.getRunCount());
        assertEquals("Wrong number of test failures", 0, result.getFailureCount());
    }

    @Test
    public void testErrorWithInvalidRunner() {
        Result result = JUnitCore.runClasses(InvalidParameterisedRunnerTest.class);
        assertEquals("Incorrect number of test discovered", 1, result.getRunCount());
        assertEquals("Incorrect number of failures", 1, result.getFailureCount());

        assertEquals("Incorrect error thrown", "Required constructor not found. Parameterized runners need a public constructor with arguments: Class, String, int, Object[]", result.getFailures().get(0).getException().getMessage());
    }

    @Test
    public void testErrorWithIncorrectVisibilityRunner() {
        Result result = JUnitCore.runClasses(IncorrectVisibilityParameterizedRunnerTest.class);
        assertEquals("Incorrect number of test discovered", 1, result.getRunCount());
        assertEquals("Incorrect number of failures", 1, result.getFailureCount());

        assertEquals("Incorrect error thrown", "Required constructor not found. Parameterized runners need a public constructor with arguments: Class, String, int, Object[]", result.getFailures().get(0).getException().getMessage());
    }

    @RunWith(Parameterized.class)
    @Parameterized.ParameterizedRunWith(ExtendedTestClassRunnerForParametersRunner.class)
    public static class ExtendedTestClassRunnerForParametersTest {

        private static int iteration = -1;
        @Parameterized.Parameter(0) public int param1;
        @Parameterized.Parameter(1) public int param2;
        @Parameterized.Parameter(2) public int param3;
        @Parameterized.Parameter(3) public int param4;
        @Parameterized.Parameter(4) public int param5;

        public ExtendedTestClassRunnerForParametersTest() {
            iteration++;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> parameters() {
            List<Object[]> params = new ArrayList<Object[]>();
            for (int i = 0; i < 5; i++) {
                Object[] paramRow = new Object[5];
                for (int j = 0; j < paramRow.length; j++) {
                    paramRow[j] = j + 1;
                }
                params.add(paramRow);
            }
            return params;
        }

        @Test
        public void testRunWithCustomRunner() {
            assertEquals("Parameter 1 should have been modified by the runner", iteration, param1);
            assertEquals("Parameter 2 should have been modified by the runner", 2 * iteration, param2);
            assertEquals("Parameter 3 should have been modified by the runner", 3 * iteration, param3);
            assertEquals("Parameter 4 should have been modified by the runner", 4 * iteration, param4);
            assertEquals("Parameter 5 should have been modified by the runner", 5 * iteration, param5);
        }
    }


    public static class ExtendedTestClassRunnerForParametersRunner extends Parameterized.TestClassRunnerForParameters {

        public ExtendedTestClassRunnerForParametersRunner(Class<?> type, String pattern, int index, Object[] parameters) throws InitializationError {
            super(type, pattern, index, changeParams(parameters, index));
        }

        private static Object[] changeParams(Object[] params, int index) {
            Object[] updated = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                updated[i] = ((Integer) params[i]) * index;
            }

            return updated;

        }

    }

    @RunWith(Parameterized.class)
    @Parameterized.ParameterizedRunWith(InvalidTestRunnerForParameters.class)
    public static class InvalidParameterisedRunnerTest {

        @Parameterized.Parameters
        public static Collection<Object[]> getParameters() {
            return Arrays.asList(new Object[][]{new Object[]{1}});
        }

        @Test
        public void testNothing() {
            //do nothing
        }
    }

    public static class InvalidTestRunnerForParameters extends BlockJUnit4ClassRunner {
       public InvalidTestRunnerForParameters(Class<?> singleParam) throws InitializationError {
          super(singleParam);
       }
    }

    @RunWith(Parameterized.class)
    @Parameterized.ParameterizedRunWith(IncorrectVisibilityParameterizedRunner.class)
    public static class IncorrectVisibilityParameterizedRunnerTest {

        @Parameterized.Parameters
        public static Collection<Object[]> getParameters() {
            return Arrays.asList(new Object[][]{new Object[]{2}});
        }

        @Test
        public void testNothing() {
            //do nothing
        }
    }

    public static class IncorrectVisibilityParameterizedRunner extends BlockJUnit4ClassRunner {
        protected IncorrectVisibilityParameterizedRunner(Class<?> testCase, String description, int iteration, Object[] params) throws InitializationError {
            super(testCase);
        }
    }
}
