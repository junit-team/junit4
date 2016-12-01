package org.junit.rules;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.function.ThrowingRunnable;

import org.hamcrest.Matcher;
import org.junit.internal.AssumptionNotSupportedException;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.MultipleFailureException;

/**
 * The ErrorCollector rule allows execution of a test to continue after the
 * first problem is found (for example, to collect _all_ the incorrect rows in a
 * table, and report them all at once):
 *
 * <pre>
 * public static class UsesErrorCollectorTwice {
 * 	&#064;Rule
 * 	public ErrorCollector collector= new ErrorCollector();
 *
 * &#064;Test
 * public void example() {
 *      collector.addError(new Throwable(&quot;first thing went wrong&quot;));
 *      collector.addError(new Throwable(&quot;second thing went wrong&quot;));
 *      collector.checkThat(getResult(), not(containsString(&quot;ERROR!&quot;)));
 *      // all lines will run, and then a combined failure logged at the end.
 *     }
 * }
 * </pre>
 *
 * Note that AssumptionViolationException (thrown by <code>org.junit.Assume</code>)
 * is not supported and causes immediate test fail.
 *
 * @since 4.7
 */
public class ErrorCollector extends Verifier {
    private List<Throwable> errors = new ArrayList<Throwable>();

    @Override
    protected void verify() throws Throwable {
        MultipleFailureException.assertEmpty(errors);
    }

    /**
     * Adds a Throwable to the table.  Execution continues, but the test will fail at the end.
     */
    public void addError(Throwable error) {
        if (error instanceof AssumptionViolatedException) {
            throw new AssumptionNotSupportedException("Assumptions are not supported in ErrorCollector", error);
        }
        errors.add(error);
    }

    /**
     * Adds a failure to the table if {@code matcher} does not match {@code value}.
     * Execution continues, but the test will fail at the end if the match fails.
     *
     * @deprecated use {@code org.hamcrest.junit.ErrorCollector.checkThat()}
     */
    @Deprecated
    public <T> void checkThat(final T value, final Matcher<T> matcher) {
        checkThat("", value, matcher);
    }

    /**
     * Adds a failure with the given {@code reason}
     * to the table if {@code matcher} does not match {@code value}.
     * Execution continues, but the test will fail at the end if the match fails.
     *
     * @deprecated use {@code org.hamcrest.junit.ErrorCollector.checkThat()}
     */
    @Deprecated
    public <T> void checkThat(final String reason, final T value, final Matcher<T> matcher) {
        checkSucceeds(new Callable<Object>() {
            public Object call() throws Exception {
                assertThat(reason, value, matcher);
                return value;
            }
        });
    }

    /**
     * Adds to the table the exception, if any, thrown from {@code callable}.
     * Execution continues, but the test will fail at the end if
     * {@code callable} threw an exception.
     */
    public <T> T checkSucceeds(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable e) {
            addError(e);
            return null;
        }
    }

    /**
     * Adds a failure to the table if {@code runnable} does not throw an
     * exception of type {@code expectedThrowable} when executed.
     * Execution continues, but the test will fail at the end if the runnable
     * does not throw an exception, or if it throws a different exception.
     *
     * @param expectedThrowable the expected type of the exception
     * @param runnable       a function that is expected to throw an exception when executed
     * @since 4.13
     */
    public void checkThrows(Class<? extends Throwable> expectedThrowable, ThrowingRunnable runnable) {
        try {
            assertThrows(expectedThrowable, runnable);
        } catch (AssumptionNotSupportedException e) {
            throw new AssumptionNotSupportedException("Assumptions are not supported in ErrorCollector", e.getCause());
        } catch (AssertionError e) {
            addError(e);
        }
    }

}
