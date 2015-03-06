package org.junit.internal.runners.statements;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class RunBefores extends Statement {

    private final Statement previous;

    private final Statement next;

    private final Object target;

    private final List<FrameworkMethod> befores;

    /**
     * @param previous The previous statement, or {@code null}.
     * @param next The next statement after before methods.
     * @param befores The list of before methods.
     * @param target
     */
    public RunBefores(final Statement previous, final Statement next,
            List<FrameworkMethod> befores, Object target) {
        this.previous = previous;
        this.next = next;
        this.befores = befores;
        this.target = target;
    }

    @Override
    public void evaluate() throws Throwable {
        if (null != previous) {
            previous.evaluate();
        }
        for (FrameworkMethod before : befores) {
            before.invokeExplosively(target);
        }
        next.evaluate();
    }
}