package org.junit.internal.requests;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runners.ParentRunner;

public class ClassRequest extends Request {
    private final Object fRunnerLock = new Object();
    private final Class<?> fTestClass;
    private final boolean fCanUseSuiteMethod;
    private volatile Runner fRunner;

    public ClassRequest(Class<?> testClass, boolean canUseSuiteMethod) {
        fTestClass = testClass;
        fCanUseSuiteMethod = canUseSuiteMethod;
    }

    public ClassRequest(Class<?> testClass) {
        this(testClass, true);
    }

    @Override
    public Runner getRunner() {
        if (fRunner == null) {
            synchronized (fRunnerLock) {
                if (fRunner == null) {
                    fRunner = new AllDefaultPossibilitiesBuilder(fCanUseSuiteMethod).safeRunnerForClass(fTestClass);
                    if(fRunner instanceof Sortable)
                        ((Sortable)fRunner).sort(Sorter.ANNOTATED_SORTER);
                }
            }
        }
        return fRunner;
    }
}