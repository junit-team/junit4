package org.junit.experimental.runners.context.statements;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.List;
import java.util.Map;

public class RunHierarchicalBefores extends Statement {
    private final Statement next;
    private final Map<Object, List<FrameworkMethod>> beforeMethods;

    public RunHierarchicalBefores(final Statement next, final Map<Object, List<FrameworkMethod>> beforeMethods) {
        this.next = next;
        this.beforeMethods = beforeMethods;
    }

    @Override
    public void evaluate() throws Throwable {
        for (Map.Entry<Object, List<FrameworkMethod>> entry : beforeMethods.entrySet())
            runBeforeMethodsForEntry(entry);
        next.evaluate();
    }

    private void runBeforeMethodsForEntry(Map.Entry<Object, List<FrameworkMethod>> entry) throws Throwable {
        for (FrameworkMethod beforeMethod : entry.getValue()) {
            final Object target = entry.getKey();
            beforeMethod.invokeExplosively(target);
        }
    }
}