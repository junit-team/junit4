package org.junit.internal.runners.model;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.MultipleFailureException;

public class EachTestNotifier {
    private final RunNotifier notifier;

    private final Description description;

    public EachTestNotifier(RunNotifier notifier, Description description) {
        this.notifier = notifier;
        this.description = description;
    }

    public void addFailure(Throwable targetException) {
        if (targetException instanceof MultipleFailureException) {
            addMultipleFailureException((MultipleFailureException) targetException);
        } else {
            notifier.fireTestFailure(new Failure(description, targetException));
        }
    }

    private void addMultipleFailureException(MultipleFailureException mfe) {
        for (Throwable each : mfe.getFailures()) {
            addFailure(each);
        }
    }

    public void addFailedAssumption(AssumptionViolatedException e) {
        notifier.fireTestAssumptionFailed(new Failure(description, e));
    }

    public void fireTestFinished() {
        notifier.fireTestFinished(description);
    }

    public void fireTestStarted() {
        notifier.fireTestStarted(description);
    }

    public void fireTestIgnored() {
        notifier.fireTestIgnored(description);
    }

    /**
     * Calls {@link RunNotifier#fireTestSuiteStarted(Description)}, passing the
     * {@link Description} that was passed to the {@code EachTestNotifier} constructor.
     * This should be called when a test suite is about to be started.
     * @see RunNotifier#fireTestSuiteStarted(Description)
     * @since 4.13
     */
    public void fireTestSuiteStarted() {
        notifier.fireTestSuiteStarted(description);
    }

    /**
     * Calls {@link RunNotifier#fireTestSuiteFinished(Description)}, passing the
     * {@link Description} that was passed to the {@code EachTestNotifier} constructor.
     * This should be called when a test suite has finished, whether the test suite succeeds
     * or fails.
     * @see RunNotifier#fireTestSuiteFinished(Description)
     * @since 4.13
     */
    public void fireTestSuiteFinished() {
        notifier.fireTestSuiteFinished(description);
    }
}