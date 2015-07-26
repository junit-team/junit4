package org.junit.runner.notification;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * If you write custom runners, you may need to notify JUnit of your progress running tests.
 * Do this by invoking the <code>RunNotifier</code> passed to your implementation of
 * {@link org.junit.runner.Runner#run(RunNotifier)}. Future evolution of this class is likely to
 * move {@link #fireTestRunStarted(Description)} and {@link #fireTestRunFinished(Result)}
 * to a separate class since they should only be called once per run.
 *
 * @since 4.0
 */
public class RunNotifier {
    private final List<RunListener> listeners = new CopyOnWriteArrayList<RunListener>();
    private volatile boolean pleaseStop = false;

    /**
     * Internal use only
     */
    public void addListener(RunListener listener) {
        if (listener == null) {
            throw new NullPointerException("Cannot add a null listener");
        }
        listeners.add(wrapIfNotThreadSafe(listener));
    }

    /**
     * Internal use only
     */
    public void removeListener(RunListener listener) {
        if (listener == null) {
            throw new NullPointerException("Cannot remove a null listener");
        }
        listeners.remove(wrapIfNotThreadSafe(listener));
    }

    /**
     * Wraps the given listener with {@link SynchronizedRunListener} if
     * it is not annotated with {@link RunListener.ThreadSafe}.
     */
    RunListener wrapIfNotThreadSafe(RunListener listener) {
        return listener.getClass().isAnnotationPresent(RunListener.ThreadSafe.class) ?
                listener : new SynchronizedRunListener(listener, this);
    }


    private abstract class SafeNotifier {
        private final List<RunListener> currentListeners;

        SafeNotifier() {
            this(listeners);
        }

        SafeNotifier(List<RunListener> currentListeners) {
            this.currentListeners = currentListeners;
        }

        void run() {
            int capacity = currentListeners.size();
            List<RunListener> safeListeners = new ArrayList<RunListener>(capacity);
            List<Failure> failures = new ArrayList<Failure>(capacity);
            for (RunListener listener : currentListeners) {
                try {
                    notifyListener(listener);
                    safeListeners.add(listener);
                } catch (Exception e) {
                    failures.add(new Failure(Description.TEST_MECHANISM, e));
                }
            }
            fireTestFailures(safeListeners, failures);
        }

        abstract protected void notifyListener(RunListener each) throws Exception;
    }

    /**
     * Do not invoke.
     */
    public void fireTestRunStarted(final Description description) {
        new SafeNotifier() {
            @Override
            protected void notifyListener(RunListener each) throws Exception {
                each.testRunStarted(description);
            }
        }.run();
    }

    /**
     * Do not invoke.
     */
    public void fireTestRunFinished(final Result result) {
        new SafeNotifier() {
            @Override
            protected void notifyListener(RunListener each) throws Exception {
                each.testRunFinished(result);
            }
        }.run();
    }

    /**
     * Invoke to tell listeners that an atomic test is about to start.
     *
     * @param description the description of the atomic test (generally a class and method name)
     * @throws StoppedByUserException thrown if a user has requested that the test run stop
     */
    public void fireTestStarted(final Description description) throws StoppedByUserException {
        if (pleaseStop) {
            throw new StoppedByUserException();
        }
        new SafeNotifier() {
            @Override
            protected void notifyListener(RunListener each) throws Exception {
                each.testStarted(description);
            }
        }.run();
    }

    /**
     * Invoke to tell listeners that an atomic test failed.
     *
     * @param failure the description of the test that failed and the exception thrown
     */
    public void fireTestFailure(Failure failure) {
        fireTestFailures(listeners, asList(failure));
    }

    private void fireTestFailures(List<RunListener> listeners,
            final List<Failure> failures) {
        if (!failures.isEmpty()) {
            new SafeNotifier(listeners) {
                @Override
                protected void notifyListener(RunListener listener) throws Exception {
                    for (Failure each : failures) {
                        listener.testFailure(each);
                    }
                }
            }.run();
        }
    }

    /**
     * Invoke to tell listeners that an atomic test flagged that it assumed
     * something false.
     *
     * @param failure the description of the test that failed and the
     * {@link org.junit.AssumptionViolatedException} thrown
     */
    public void fireTestAssumptionFailed(final Failure failure) {
        new SafeNotifier() {
            @Override
            protected void notifyListener(RunListener each) throws Exception {
                each.testAssumptionFailure(failure);
            }
        }.run();
    }

    /**
     * Invoke to tell listeners that an atomic test was ignored.
     *
     * @param description the description of the ignored test
     */
    public void fireTestIgnored(final Description description) {
        new SafeNotifier() {
            @Override
            protected void notifyListener(RunListener each) throws Exception {
                each.testIgnored(description);
            }
        }.run();
    }

    /**
     * Invoke to tell listeners that an atomic test finished. Always invoke
     * this method if you invoke {@link #fireTestStarted(Description)}
     * as listeners are likely to expect them to come in pairs.
     *
     * @param description the description of the test that finished
     */
    public void fireTestFinished(final Description description) {
        new SafeNotifier() {
            @Override
            protected void notifyListener(RunListener each) throws Exception {
                each.testFinished(description);
            }
        }.run();
    }

    /**
     * Ask that the tests run stop before starting the next test. Phrased politely because
     * the test currently running will not be interrupted. It seems a little odd to put this
     * functionality here, but the <code>RunNotifier</code> is the only object guaranteed
     * to be shared amongst the many runners involved.
     */
    public void pleaseStop() {
        pleaseStop = true;
    }

    /**
     * Internal use only. The Result's listener must be first.
     */
    public void addFirstListener(RunListener listener) {
        if (listener == null) {
            throw new NullPointerException("Cannot add a null listener");
        }
        listeners.add(0, wrapIfNotThreadSafe(listener));
    }
}
