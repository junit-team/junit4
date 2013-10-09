package org.junit.tests.junit3compatibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import junit.extensions.TestDecorator;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;

public class JUnit38ClassRunnerTest {
    public static class MyTest extends TestCase {
        public void testA() {

        }
    }

    @Test
    public void plansDecoratorCorrectly() {
        JUnit38ClassRunner runner = new JUnit38ClassRunner(new TestDecorator(new TestSuite(MyTest.class)));
        assertEquals(1, runner.testCount());
    }

    public static class AnnotatedTest {
        @Test
        public void foo() {
            Assert.fail();
        }
    }

    @Test
    public void canUnadaptAnAdapter() {
        JUnit38ClassRunner runner = new JUnit38ClassRunner(new JUnit4TestAdapter(AnnotatedTest.class));
        Result result = new JUnitCore().run(runner);
        Failure failure = result.getFailures().get(0);
        assertEquals(Description.createTestDescription(AnnotatedTest.class, "foo"), failure.getDescription());
    }

    static int count;

    static public class OneTest extends TestCase {
        public void testOne() {
        }
    }

    @Test
    public void testListener() throws Exception {
        JUnitCore runner = new JUnitCore();
        RunListener listener = new RunListener() {
            @Override
            public void testStarted(Description description) {
                assertEquals(Description.createTestDescription(OneTest.class, "testOne"),
                        description);
                count++;
            }
        };

        runner.addListener(listener);
        count = 0;
        Result result = runner.run(OneTest.class);
        assertEquals(1, count);
        assertEquals(1, result.getRunCount());
    }

    public static class ClassWithInvalidMethod extends TestCase {
        @SuppressWarnings("unused")
        private void testInvalid() {
        }
    }

    @Test
    public void invalidTestMethodReportedCorrectly() {
        Result result = JUnitCore.runClasses(ClassWithInvalidMethod.class);
        Failure failure = result.getFailures().get(0);
        assertEquals("warning", failure.getDescription().getMethodName());
        assertEquals("junit.framework.TestSuite$1", failure.getDescription().getClassName());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface MyAnnotation {
    }

    public static class JUnit3ClassWithAnnotatedMethod extends TestCase {
        @MyAnnotation
        public void testAnnotated() {
        }

        public void testNotAnnotated() {
        }
    }

    public static class DerivedAnnotatedMethod extends JUnit3ClassWithAnnotatedMethod {
    }

    @Test
    public void getDescriptionWithAnnotation() {
        JUnit38ClassRunner runner = new JUnit38ClassRunner(JUnit3ClassWithAnnotatedMethod.class);
        assertAnnotationFiltering(runner);
    }
    
    @Test
    public void getDescriptionWithAnnotationInSuper() {
        JUnit38ClassRunner runner = new JUnit38ClassRunner(DerivedAnnotatedMethod.class);
        assertAnnotationFiltering(runner);
    }

    private void assertAnnotationFiltering(JUnit38ClassRunner runner) {
        Description d = runner.getDescription();
        assertEquals(2, d.testCount());
        for (Description methodDesc : d.getChildren()) {
            if (methodDesc.getMethodName().equals("testAnnotated")) {
                assertNotNull(methodDesc.getAnnotation(MyAnnotation.class));
            } else {
                assertNull(methodDesc.getAnnotation(MyAnnotation.class));
            }
        }
    }

    public static class RejectAllTestsFilter extends Filter {
        @Override
        public boolean shouldRun(Description description) {
            return description.isSuite();
        }

        @Override
        public String describe() {
            return "filter all";
        }
    }

    /**
     * Test that NoTestsRemainException is thrown when all methods have been filtered.
     */
    @Test(expected = NoTestsRemainException.class) 
    public void filterNoTestsRemain() throws NoTestsRemainException {
        JUnit38ClassRunner runner = new JUnit38ClassRunner(OneTest.class);
        runner.filter(new RejectAllTestsFilter());  
    }
}
