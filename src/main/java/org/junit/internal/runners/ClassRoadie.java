package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * @deprecated Included for backwards compatibility with JUnit 4.4. Will be
 *             removed in the next major release. Please use
 *             {@link BlockJUnit4ClassRunner} in place of {@link JUnit4ClassRunner}.
 */
@Deprecated
public class ClassRoadie {
    private RunNotifier notifier;
    private TestClass testClass;
    private Description description;
    private final Runnable runnable;

    public ClassRoadie(RunNotifier notifier, TestClass testClass,
            Description description, Runnable runnable) {
        this.notifier = notifier;
        this.testClass = testClass;
        this.description = description;
        this.runnable = runnable;
    }

    protected void runUnprotected() {
        runnable.run();
    }

    protected void addFailure(Throwable targetException) {
        notifier.fireTestFailure(new Failure(description, targetException));
    }

    public void runProtected() {
        try {
            runBefores();
            runUnprotected();
        } catch (FailedBefore e) {
        } finally {
            runAfters();
        }
    }

    private void runBefores() throws FailedBefore {
        try {
            try {
                List<Method> befores = testClass.getBefores();
                for (Method before : befores) {
                    before.invoke(null);
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } catch (AssumptionViolatedException e) {
            throw new FailedBefore();
        } catch (Throwable e) {
            addFailure(e);
            throw new FailedBefore();
        }
    }

    private void runAfters() {
        List<Method> afters = testClass.getAfters();
        for (Method after : afters) {
            try {
                after.invoke(null);
            } catch (InvocationTargetException e) {
                addFailure(e.getTargetException());
            } catch (Throwable e) {
                addFailure(e); // Untested, but seems impossible
            }
        }
    }
}
