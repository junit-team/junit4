package org.junit.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * TestWatcher is a base class for Rules that take note of the testing
 * action, without modifying it. For example, this class will keep a log of each
 * passing and failing test:
 *
 * <pre>
 * public static class WatchmanTest {
 *  private static String watchedLog;
 *
 *  &#064;Rule
 *  public TestWatcher watchman= new TestWatcher() {
 *      &#064;Override
 *      protected void failed(Throwable e, Description description) {
 *          watchedLog+= description + &quot;\n&quot;;
 *      }
 *
 *      &#064;Override
 *      protected void succeeded(Description description) {
 *          watchedLog+= description + &quot; &quot; + &quot;success!\n&quot;;
 *         }
 *     };
 *
 *  &#064;Test
 *  public void fails() {
 *      fail();
 *  }
 *
 *  &#064;Test
 *  public void succeeds() {
 *     }
 * }
 * </pre>
 *
 * @since 4.9
 */
public abstract class TestWatcher implements TestRule {
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                List<Throwable> errors = new ArrayList<Throwable>();

                startingQuietly(description, errors);
                try {
                    base.evaluate();
                    succeededQuietly(description, errors);
                } catch (AssumptionViolatedException e) {
                    errors.add(e);
                    skippedQuietly(e, description, errors);
                } catch (Throwable t) {
                    errors.add(t);
                    failedQuietly(t, description, errors);
                } finally {
                    finishedQuietly(description, errors);
                }

                MultipleFailureException.assertEmpty(errors);
            }
        };
    }

    private void succeededQuietly(Description description,
            List<Throwable> errors) {
        try {
            succeeded(description);
        } catch (Throwable t) {
            errors.add(t);
        }
    }

    private void failedQuietly(Throwable t, Description description,
            List<Throwable> errors) {
        try {
            failed(t, description);
        } catch (Throwable t1) {
            errors.add(t1);
        }
    }

    private void skippedQuietly(AssumptionViolatedException e, Description description,
            List<Throwable> errors) {
        try {
            skipped(e, description);
        } catch (Throwable t) {
            errors.add(t);
        }
    }

    private void startingQuietly(Description description,
            List<Throwable> errors) {
        try {
            starting(description);
        } catch (Throwable t) {
            errors.add(t);
        }
    }

    private void finishedQuietly(Description description,
            List<Throwable> errors) {
        try {
            finished(description);
        } catch (Throwable t) {
            errors.add(t);
        }
    }

    /**
     * Invoked when a test succeeds
     */
    protected void succeeded(Description description) {
    }

    /**
     * Invoked when a test fails
     */
    protected void failed(Throwable e, Description description) {
    }

    /**
     * Invoked when a test is skipped due to a failed assumption.
     */
    protected void skipped(AssumptionViolatedException e, Description description) {
    }

    /**
     * Invoked when a test is about to start
     */
    protected void starting(Description description) {
    }

    /**
     * Invoked when a test method finishes (whether passing or failing)
     */
    protected void finished(Description description) {
    }
}
