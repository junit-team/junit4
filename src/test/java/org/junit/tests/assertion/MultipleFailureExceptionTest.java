package org.junit.tests.assertion;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.TestCouldNotBeSkippedException;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.MultipleFailureException;


/**
 * Tests for {@link org.junit.runners.model.MultipleFailureException}
 *
 * @author kcooney@google.com (Kevin Cooney)
 */
public class MultipleFailureExceptionTest {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    public void assertEmptyDoesNotThrowForEmptyList() throws Exception {
        MultipleFailureException.assertEmpty(Collections.<Throwable>emptyList());
    }

    @Test
    public void assertEmptyRethrowsSingleRuntimeException() throws Exception {
        Throwable exception= new ExpectedException("pesto");
        List<Throwable> errors= Collections.singletonList(exception);
        try {
            MultipleFailureException.assertEmpty(errors);
            fail();
        } catch (ExpectedException e) {
            assertSame(e, exception);
        }
    }

    @Test
    public void assertEmptyRethrowsSingleError() throws Exception {
        Throwable exception= new AnnotationFormatError("changeo");
        List<Throwable> errors= Collections.singletonList(exception);
        try {
            MultipleFailureException.assertEmpty(errors);
            fail();
        } catch (AnnotationFormatError e) {
            assertSame(e, exception);
        }
    }

    @Test
    public void assertEmptyThrowsMultipleFailureExceptionForManyThrowables() throws Exception {
        List<Throwable> errors = new ArrayList<Throwable>();
        errors.add(new ExpectedException("basil"));
        errors.add(new RuntimeException("garlic"));

        try {
            MultipleFailureException.assertEmpty(errors);
            fail();
        } catch (MultipleFailureException expected) {
            assertThat(expected.getFailures(), equalTo(errors));
            assertTrue(expected.getMessage().startsWith("There were 2 errors:" + LINE_SEPARATOR));
            assertTrue(expected.getMessage().contains("ExpectedException(basil)" + LINE_SEPARATOR));
            assertTrue(expected.getMessage().contains("RuntimeException(garlic)"));
        }
    }

    @Test
    public void assertEmptyErrorListConstructorFailure() {
        try {
            new MultipleFailureException(Collections.<Throwable>emptyList());
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    containsString("List of Throwables must not be empty"));
        }
    }

    @Test
    public void assertEmptyWrapsAssumptionFailuresForManyThrowables() throws Exception {
        List<Throwable> errors = new ArrayList<Throwable>();
        AssumptionViolatedException assumptionViolatedException = new AssumptionViolatedException("skip it");
        errors.add(assumptionViolatedException);
        errors.add(new RuntimeException("garlic"));

        try {
            MultipleFailureException.assertEmpty(errors);
            fail();
        } catch (MultipleFailureException expected) {
            assertThat(expected.getFailures().size(), equalTo(2));
            assertTrue(expected.getMessage().startsWith("There were 2 errors:" + LINE_SEPARATOR));
            assertTrue(expected.getMessage().contains("TestCouldNotBeSkippedException(Test could not be skipped"));
            assertTrue(expected.getMessage().contains("RuntimeException(garlic)"));
            Throwable first = expected.getFailures().get(0);
            assertThat(first, instanceOf(TestCouldNotBeSkippedException.class));
            Throwable cause = ((TestCouldNotBeSkippedException) first).getCause();
            assertThat(cause, instanceOf(AssumptionViolatedException.class));
            assertThat((AssumptionViolatedException) cause, CoreMatchers.sameInstance(assumptionViolatedException));
        }
    }

    private static class ExpectedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ExpectedException(String message) {
            super(message);
        }
    }
}
