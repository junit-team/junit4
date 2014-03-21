package org.junit.internal.runners.statements;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class RunBefores extends Statement {
    private final Statement next;

    private final Object target;

    private final List<FrameworkMethod> befores;

    public RunBefores(Statement next, List<FrameworkMethod> befores, Object target) {
        this.next = next;
        this.befores = befores;
        this.target = target;
    }

    @Override
    public void evaluate() throws Throwable {
        for (FrameworkMethod before : befores) {
            before.invokeExplosively(target);
        }
        next.evaluate();
    }
}