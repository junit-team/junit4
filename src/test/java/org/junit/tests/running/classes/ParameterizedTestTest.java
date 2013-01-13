package org.junit.tests.running.classes;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.InitializationError;

public class ParameterizedTestTest {
    @RunWith(Parameterized.class)
    static public class FibonacciTest {
        @Parameters(name = "{index}: fib({0})={1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{{0, 0}, {1, 1}, {2, 1},
                    {3, 2}, {4, 3}, {5, 5}, {6, 8}});
        }

        private final int fInput;

        private final int fExpected;

        public FibonacciTest(int input, int expected) {
            fInput = input;
            fExpected = expected;
        }

        @Test
        public void test() {
            assertEquals(fExpected, fib(fInput));
        }

        private int fib(int x) {
            return 0;
        }
    }

    @Test
    public void count() {
        Result result = JUnitCore.runClasses(FibonacciTest.class);
        assertEquals(7, result.getRunCount());
        assertEquals(6, result.getFailureCount());
    }

    @Test
    public void failuresNamedCorrectly() {
        Result result = JUnitCore.runClasses(FibonacciTest.class);
        assertEquals(
                "test[1: fib(1)=1](" + FibonacciTest.class.getName() + ")",
                result.getFailures().get(0).getTestHeader());
    }

    @Test
    public void countBeforeRun() throws Exception {
        Runner runner = Request.aClass(FibonacciTest.class).getRunner();
        assertEquals(7, runner.testCount());
    }

    @Test
    public void plansNamedCorrectly() throws Exception {
        Runner runner = Request.aClass(FibonacciTest.class).getRunner();
        Description description = runner.getDescription();
        assertEquals("[0: fib(0)=0]", description.getChildren().get(0)
                .getDisplayName());
    }

    @RunWith(Parameterized.class)
    public static class ParameterizedWithoutSpecialTestname {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{{3}, {3}});
        }

        public ParameterizedWithoutSpecialTestname(Object something) {
        }

        @Test
        public void testSomething() {
        }
    }

    @Test
    public void usesIndexAsTestName() {
        Runner runner = Request
                .aClass(ParameterizedWithoutSpecialTestname.class).getRunner();
        Description description = runner.getDescription();
        assertEquals("[1]", description.getChildren().get(1).getDisplayName());
    }

    @RunWith(Parameterized.class)
    static public class FibonacciWithParameterizedFieldTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{{0, 0}, {1, 1}, {2, 1},
                    {3, 2}, {4, 3}, {5, 5}, {6, 8}});
        }

        @Parameter(0)
        public int fInput;

        @Parameter(1)
        public int fExpected;

        @Test
        public void test() {
            assertEquals(fExpected, fib(fInput));
        }

        private int fib(int x) {
            return 0;
        }
    }

    @Test
    public void countWithParameterizedField() {
        Result result = JUnitCore.runClasses(FibonacciWithParameterizedFieldTest.class);
        assertEquals(7, result.getRunCount());
        assertEquals(6, result.getFailureCount());
    }

    @Test
    public void failuresNamedCorrectlyWithParameterizedField() {
        Result result = JUnitCore.runClasses(FibonacciWithParameterizedFieldTest.class);
        assertEquals(String
                .format("test[1](%s)", FibonacciWithParameterizedFieldTest.class.getName()), result
                .getFailures().get(0).getTestHeader());
    }

    @Test
    public void countBeforeRunWithParameterizedField() throws Exception {
        Runner runner = Request.aClass(FibonacciWithParameterizedFieldTest.class).getRunner();
        assertEquals(7, runner.testCount());
    }

    @Test
    public void plansNamedCorrectlyWithParameterizedField() throws Exception {
        Runner runner = Request.aClass(FibonacciWithParameterizedFieldTest.class).getRunner();
        Description description = runner.getDescription();
        assertEquals("[0]", description.getChildren().get(0).getDisplayName());
    }

    @RunWith(Parameterized.class)
    static public class BadIndexForAnnotatedFieldTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{{0}});
        }

        @Parameter(2)
        public int fInput;

        public int fExpected;

        @Test
        public void test() {
            assertEquals(fExpected, fib(fInput));
        }

        private int fib(int x) {
            return 0;
        }
    }

    @Test
    public void failureOnInitialization() {
        Result result = JUnitCore.runClasses(BadIndexForAnnotatedFieldTest.class);
        assertEquals(2, result.getFailureCount());
        List<Failure> failures = result.getFailures();
        assertEquals("Invalid @Parameter value: 2. @Parameter fields counted: 1. Please use an index between 0 and 0.",
                failures.get(0).getException().getMessage());
        assertEquals("@Parameter(0) is never used.", failures.get(1).getException().getMessage());
    }

    @RunWith(Parameterized.class)
    static public class BadNumberOfAnnotatedFieldTest {
        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{{0, 0}});
        }

        @Parameter(0)
        public int fInput;

        public int fExpected;

        @Test
        public void test() {
            assertEquals(fExpected, fib(fInput));
        }

        private int fib(int x) {
            return 0;
        }
    }

    @Test
    public void numberOfFieldsAndParametersShouldMatch() {
        Result result = JUnitCore.runClasses(BadNumberOfAnnotatedFieldTest.class);
        assertEquals(1, result.getFailureCount());
        List<Failure> failures = result.getFailures();
        assertTrue(failures.get(0).getException().getMessage().contains("Wrong number of parameters and @Parameter fields. @Parameter fields counted: 1, available parameters: 2."));
    }

    private static String fLog;

    @RunWith(Parameterized.class)
    static public class BeforeAndAfter {
        @BeforeClass
        public static void before() {
            fLog += "before ";
        }

        @AfterClass
        public static void after() {
            fLog += "after ";
        }

        public BeforeAndAfter(int x) {

        }

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{{3}});
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void beforeAndAfterClassAreRun() {
        fLog = "";
        JUnitCore.runClasses(BeforeAndAfter.class);
        assertEquals("before after ", fLog);
    }

    @RunWith(Parameterized.class)
    static public class EmptyTest {
        @BeforeClass
        public static void before() {
            fLog += "before ";
        }

        @AfterClass
        public static void after() {
            fLog += "after ";
        }
    }

    @Test
    public void validateClassCatchesNoParameters() {
        Result result = JUnitCore.runClasses(EmptyTest.class);
        assertEquals(1, result.getFailureCount());
    }

    @RunWith(Parameterized.class)
    static public class IncorrectTest {
        @Test
        public int test() {
            return 0;
        }

        @Parameters
        public static Collection<Object[]> data() {
            return Collections.singletonList(new Object[]{1});
        }
    }

    @Test
    public void failuresAddedForBadTestMethod() throws Exception {
        Result result = JUnitCore.runClasses(IncorrectTest.class);
        assertEquals(1, result.getFailureCount());
    }

    @RunWith(Parameterized.class)
    static public class ProtectedParametersTest {
        @Parameters
        protected static Collection<Object[]> data() {
            return Collections.emptyList();
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void meaningfulFailureWhenParametersNotPublic() throws Exception {
        Result result = JUnitCore.runClasses(ProtectedParametersTest.class);
        String expected = String.format(
                "No public static parameters method on class %s",
                ProtectedParametersTest.class.getName());
        assertEquals(expected, result.getFailures().get(0).getMessage());
    }

    @RunWith(Parameterized.class)
    static public class WrongElementType {
        @Parameters
        public static Iterable<String> data() {
            return Arrays.asList("a", "b", "c");
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void meaningfulFailureWhenParametersAreNotArrays() {
        assertThat(
                testResult(WrongElementType.class).toString(),
                containsString("WrongElementType.data() must return an Iterable of arrays."));
    }

    @RunWith(Parameterized.class)
    static public class ParametersNotIterable {
        @Parameters
        public static String data() {
            return "foo";
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void meaningfulFailureWhenParametersAreNotAnIterable() {
        assertThat(
                testResult(ParametersNotIterable.class).toString(),
                containsString("ParametersNotIterable.data() must return an Iterable of arrays."));
    }

    @RunWith(Parameterized.class)
    static public class PrivateConstructor {
        private PrivateConstructor(int x) {

        }

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{{3}});
        }

        @Test
        public void aTest() {
        }
    }

    @Test(expected = InitializationError.class)
    public void exceptionWhenPrivateConstructor() throws Throwable {
        new Parameterized(PrivateConstructor.class);
    }
}