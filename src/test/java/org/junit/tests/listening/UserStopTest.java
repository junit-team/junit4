package org.junit.tests.listening;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

public class UserStopTest {
    private RunNotifier runNotifier;
    private static final List<String> invocations = new ArrayList<String>();

    @Before
    public void createNotifier() {
        invocations.clear();
        
        runNotifier = new RunNotifier();
        runNotifier.pleaseStop();
    }

    @Test(expected = StoppedByUserException.class)
    public void userStop() {
        runNotifier.fireTestStarted(null);
    }

    public static class OneTest {
        @Test
        public void foo() {
        }
    }

    @Test(expected = StoppedByUserException.class)
    public void stopClassRunner() throws Exception {
        Request.aClass(OneTest.class).getRunner().run(runNotifier);
    }
    
    @RunWith(Suite.class)
    @SuiteClasses({TestClassWithExceptionInAfterClass.class,
        TestClassThatShouldNotBeExecuted.class})
    public static class SuiteExample {}
    
    public static class TestClassWithExceptionInAfterClass {
        @Test
        public void test() {}
        
        @AfterClass
        public static void afterClass() {
            throw new RuntimeException("Exception");
        }
    }
    
    public static class TestClassThatShouldNotBeExecuted {
        @BeforeClass
        public static void beforeClass() {
            invocations.add("beforeClass");
        }
        
        @Test
        public void test() {
            invocations.add("test");
        }
        
        @AfterClass
        public static void afterClass() {
            invocations.add("afterClass");
        }
    }
    
    /**
     * Verifies that stop request is properly handled for suite execution in 
     * case of additional execution errors coming from {@link AfterClass} 
     * method or {@link ClassRule}, i.e.: <br />
     * - no methods from subsequent classes should be executed <br />
     * - listeners still should be notified about execution errors except 
     * {@link StoppedByUserException}.
     */
    @Test
    public void testHandlingStopRequestWithAdditionalExecutionErrors() {
        final List<String> failures = new ArrayList<String>();
        
        runNotifier.addListener(new RunListener() {
            @Override
            public void testFailure(Failure failure) throws Exception {
                failures.add(failure.getMessage());
            }
        });
        
        try {
            Request.aClass(SuiteExample.class).getRunner().run(runNotifier);
            fail();
        } catch (StoppedByUserException e) {
            assertEquals(0, invocations.size());
            assertEquals(Arrays.asList("Exception"), failures);
        }
    }
}
