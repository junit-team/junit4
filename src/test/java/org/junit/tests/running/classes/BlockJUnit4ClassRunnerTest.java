package org.junit.tests.running.classes;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.tests.mock.MockTestRunner;
import org.junit.TestCase;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class BlockJUnit4ClassRunnerTest {
    public static class OuterClass {
        public class Enclosed {
            @Test
            public void test() {
            }
        }
    }

    @Test
    public void detectNonStaticEnclosedClass() throws Exception {
        try {
            new BlockJUnit4ClassRunner(OuterClass.Enclosed.class);
        } catch (InitializationError e) {
            List<Throwable> causes = e.getCauses();
            assertEquals("Wrong number of causes.", 1, causes.size());
            assertEquals(
                    "Wrong exception.",
                    "The inner class org.junit.tests.running.classes.BlockJUnit4ClassRunnerTest$OuterClass$Enclosed is not static.",
                    causes.get(0).getMessage());
        }
    }
    
    public static class ClassWithNoTestCaseSpecialisations {
        @Test
        public void test1() {
        }
        
        @Test
        public void test2() {
        }
    }
    
    @Test
    public void noTestCaseSpecialisationsRegressionTest() throws InitializationError {
        MockTestRunner runner = MockTestRunner.runTestsOf(ClassWithNoTestCaseSpecialisations.class);
        
        assertEquals(2, runner.getTestFinishedCount());

        runner.assertTestsStartedByName("test1", "test2");
    }
    
    public static class ClassWithTestCaseSpecialisation {
        @Test(cases={
                @TestCase({"a","b","ab"}),
                @TestCase({"J","Unit","JUnit"}),
                @TestCase({"J","Unit","Unit"})})
        public void concatenation(String left, String right, String expected) {
            assertThat(left + right, is(expected));
        }
    }
    
    @Test
    public void testMethodIsRunForEachTestCase() throws InitializationError {
        MockTestRunner runner = MockTestRunner.runTestsOf(ClassWithTestCaseSpecialisation.class);
        
        assertEquals(3, runner.getTestStartedCount());
        assertEquals(1, runner.getTestFailureCount());

        runner.assertTestsStartedByName("concatenation[a,b,ab]",
                "concatenation[J,Unit,JUnit]", "concatenation[J,Unit,Unit]");
    }   
    
    public static class ClassWithInvalidNumberOfArgumentsInTestCaseSpecialisation {
        @Test(cases={
                @TestCase({"a","b","ab"}),
                @TestCase({"J","Unit"})}) // one parameter short
        public void concatenation(String left, String right, String expected) {
        }
    }
    
    @Test(expected = InitializationError.class)
    public void cannotUseTestCaseWithWrongNumberOfArguments() throws InitializationError {
        MockTestRunner.runTestsOf(ClassWithInvalidNumberOfArgumentsInTestCaseSpecialisation.class);
    }
    
    public static class ClassWhereFunctionHasUnsupportedInputParameter{ 
        @Test(cases={
                @TestCase({"JUnit"})}) 
        public void wishfulMethod(ArrayList<String> list) {
        }
    }
    
    @Test(expected = InitializationError.class)
    public void cannotUseTestCaseUnsupportedParameterType() throws InitializationError {
        MockTestRunner.runTestsOf(ClassWhereFunctionHasUnsupportedInputParameter.class);
    }
    
    public static class ValidTestCaseConversions {
        @Test(cases=@TestCase({"a","a"}))
        public void string(String expected, String actual) {
            assertEquals(expected, actual);
        }
        
        @Test(cases=@TestCase({"1","2","3"}))
        public void integerAddition(int left, Integer right, int expected) {
            assertEquals(expected, left + right.intValue());
        }
        
        @Test(cases=@TestCase({"1.1","2.2","3.3"}))
        public void doubleAddition(double left, Double right, double expected) {
            assertEquals(expected, left + right.doubleValue(), 0.0001f);
        }
        
        @Test(cases=@TestCase({"true", "true"}))
        public void booleanComparison(boolean expected, Boolean actual) {
            assertEquals(expected, actual.booleanValue());
        }
        
        @Test(cases=@TestCase({"2","3","5"}))
        public void longAddition(long left, Long right, long expected) {
            assertEquals(expected, left + right.intValue());
        }
    }
    
    @Test
    public void parametersConvertedCorrectlyFromTestData() throws InitializationError {
        MockTestRunner runner = MockTestRunner.runTestsOf(ValidTestCaseConversions.class);
        
        runner.assertTestsStartedByName("string[a,a]",
                "integerAddition[1,2,3]",
                "doubleAddition[1.1,2.2,3.3]",
                "booleanComparison[true,true]",
                "longAddition[2,3,5]");        
        
        assertEquals(5, runner.getTestStartedCount());
        assertEquals(5, runner.getTestFinishedCount());
        assertEquals(0, runner.getTestFailureCount());

    }
    
    
}