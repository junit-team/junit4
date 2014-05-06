package org.junit.tests.experimental;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import static org.junit.testsupport.EventCollectorMatchers.hasSingleAssumptionFailure;
import static org.junit.testsupport.EventCollectorMatchers.hasSingleAssumptionFailureWithMessage;

import java.util.List;

import org.junit.Assume;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.testsupport.EventCollector;

public class AssumptionTest {
    private final static String DUMMY_MESSAGE = "Some random DUMMY_MESSAGE string.";

    public static class HasFailingAssumption {
        @Test
        public void assumptionsFail() {
            assumeThat(3, is(4));
            fail();
        }
    }

    @Test
    public void failedAssumptionsMeanPassing() {
        Result result = JUnitCore.runClasses(HasFailingAssumption.class);
        assertThat(result.getRunCount(), is(1));
        assertThat(result.getIgnoreCount(), is(0));
        assertThat(result.getFailureCount(), is(0));
    }

    @Test
    public void failedAssumptionsCanBeDetectedByListeners() {
        EventCollector eventCollector = runTestClass(HasFailingAssumption.class);
        assertThat(eventCollector, hasSingleAssumptionFailure());
    }

    public static class HasPassingAssumption {
        @Test
        public void assumptionsFail() {
            assumeThat(3, is(3));
            fail();
        }
    }

    @Test
    public void passingAssumptionsScootThrough() {
        Result result = JUnitCore.runClasses(HasPassingAssumption.class);
        assertThat(result.getRunCount(), is(1));
        assertThat(result.getIgnoreCount(), is(0));
        assertThat(result.getFailureCount(), is(1));
    }

    @Test
    public void assumeThatWorks() {
        try {
            assumeThat(1, is(2));
            fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeThatPasses() {
        assumeThat(1, is(1));
        assertCompletesNormally();
    }

    @Test
    public void assumeThatPassesOnStrings() {
        assumeThat("x", is("x"));
        assertCompletesNormally();
    }

    @Test
    public void assumeNotNullThrowsException() {
        Object[] objects = {1, 2, null};
        try {
            assumeNotNull(objects);
            fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeNotNullThrowsExceptionForNullArray() {
        try {
            assumeNotNull((Object[]) null);
            fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeNotNullPasses() {
        Object[] objects = {1, 2};
        assumeNotNull(objects);
        assertCompletesNormally();
    }

    @Test
    public void assumeNotNullIncludesParameterList() {
        try {
            Object[] objects = {1, 2, null};
            assumeNotNull(objects);
        } catch (AssumptionViolatedException e) {
            assertThat(e.getMessage(), containsString("1, 2, null"));
        } catch (Exception e) {
            fail("Should have thrown AssumptionViolatedException");
        }
    }

    @Test
    public void assumeNoExceptionThrows() {
        final Throwable exception = new NullPointerException();
        try {
            assumeNoException(exception);
            fail("Should have thrown exception");
        } catch (AssumptionViolatedException e) {
            assertThat(e.getCause(), is(exception));
        }
    }

    private void assertCompletesNormally() {
    }

    @Test
    public void assumeTrueWorks() {
        try {
            Assume.assumeTrue(false);
            fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    public static class HasFailingAssumeInBefore {
        @Before
        public void checkForSomethingThatIsntThere() {
            assumeTrue(false);
        }

        @Test
        public void failing() {
            fail();
        }
    }

    @Test
    public void failingAssumptionInBeforePreventsTestRun() {
        assertThat(testResult(HasFailingAssumeInBefore.class), isSuccessful());
    }

    public static class HasFailingAssumeInBeforeClass {
        @BeforeClass
        public static void checkForSomethingThatIsntThere() {
            assumeTrue(false);
        }

        @Test
        public void failing() {
            fail();
        }
    }

    @Test
    public void failingAssumptionInBeforeClassIgnoresClass() {
        assertThat(testResult(HasFailingAssumeInBeforeClass.class), isSuccessful());
    }

    public static class AssumptionFailureInConstructor {
        public AssumptionFailureInConstructor() {
            assumeTrue(false);
        }

        @Test
        public void shouldFail() {
            fail();
        }
    }

    @Test
    public void failingAssumptionInConstructorIgnoresClass() {
        assertThat(testResult(AssumptionFailureInConstructor.class), isSuccessful());
    }

    @Test(expected = IllegalArgumentException.class)
    public void assumeWithExpectedException() {
        assumeTrue(false);
    }

    final static Throwable e = new Throwable();

    /**
     * @see AssumptionTest#assumptionsWithMessage()
     */
    public static class HasAssumeWithMessage {
        @Test
        public void testMethod() {
            assumeTrue(DUMMY_MESSAGE, false);
        }
    }

    @Test
    public void assumptionsWithMessage() {
        EventCollector eventCollector = runTestClass(HasAssumeWithMessage.class);
        assertThat(eventCollector,
                hasSingleAssumptionFailureWithMessage(DUMMY_MESSAGE));
    }

    /**
     * @see AssumptionTest#assumptionsWithMessageAndCause()
     */
    public static class HasAssumeWithMessageAndCause {
        @Test
        public void testMethod() {
            assumeNoException(DUMMY_MESSAGE, e);
        }
    }

    @Test
    public void assumptionsWithMessageAndCause() {
        List<Failure> failures = runTestClass(
                HasAssumeWithMessageAndCause.class).getAssumptionFailures();
        assertTrue(failures.get(0).getMessage().contains(DUMMY_MESSAGE));
        assertSame(failures.get(0).getException().getCause(), e);
    }

    public static class HasFailingAssumptionWithMessage {
        @Test
        public void assumptionsFail() {
            assumeThat(DUMMY_MESSAGE, 3, is(4));
            fail();
        }
    }

    @Test
    public void failedAssumptionsWithMessage() {
        EventCollector eventCollector = runTestClass(HasFailingAssumptionWithMessage.class);
        assertThat(eventCollector,
                hasSingleAssumptionFailureWithMessage(startsWith(DUMMY_MESSAGE)));
    }

    /**
     * Helper method that runs tests on <code>clazz</code> and returns any
     * {@link Failure} objects that were {@link AssumptionViolatedException}s.
     */
    private static EventCollector runTestClass(Class<?> clazz) {
        EventCollector eventCollector = new EventCollector();
        JUnitCore core = new JUnitCore();
        core.addListener(eventCollector);
        core.run(clazz);
        return eventCollector;
    }
}
