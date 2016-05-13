package org.junit.tests.assertion;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runners.model.MultipleFailureException;


/**
 * Tests for {@link org.junit.runners.model.MultipleFailureException}
 *
 * @author kcooney@google.com (Kevin Cooney)
 */
public class MultipleFailureExceptionTest {

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
            assertThat(expected.getMessage(), allOf(
                    startsWith("There were 2 errors:\n"),
                    containsString("ExpectedException: basil\n"),
                    containsString("RuntimeException: garlic\n")
            ));
        }
    }

    @Test
    public void getMessageShouldGiveTheStacktraceOfEachFailure() throws Exception {
        List<Throwable> failures = asList(new Throwable("first failure"), new RuntimeException("second failure"));
        MultipleFailureException sut = new MultipleFailureException(failures);

        assertThat(sut.getMessage(), allOf(
                startsWith("There were 2 errors:\n"),
                containsString("1. java.lang.Throwable: first failure\n" +
                        "\tat org.junit.tests.assertion.MultipleFailureExceptionTest.getMessageShouldGiveTheStacktraceOfEachFailure"),
                containsString("2. java.lang.RuntimeException: second failure\n" +
                        "\tat org.junit.tests.assertion.MultipleFailureExceptionTest.getMessageShouldGiveTheStacktraceOfEachFailure")
                )
        );
    }

    private static class ExpectedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ExpectedException(String message) {
            super(message);
        }
    }
}
