package org.junit.experimental.runners.context.statements;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunHierarchicalAfters extends Statement {
    private final Statement next;
    private final Map<Object, List<FrameworkMethod>> afterMethods;

    public RunHierarchicalAfters(final Statement next, final Map<Object, List<FrameworkMethod>> afterMethods) {
        this.next = next;
        this.afterMethods = afterMethods;
    }

    @Override
    public void evaluate() throws Throwable {
        final List<Throwable> errors = new ArrayList<Throwable>();
        try {
            next.evaluate();
        } catch (Throwable e) {
            errors.add(e);
        } finally {
            for (Map.Entry<Object, List<FrameworkMethod>> entry : afterMethods.entrySet())
                runAfterMethodsForEntry(errors, entry);
        }
        MultipleFailureException.assertEmpty(errors);
    }

    private void runAfterMethodsForEntry(
            final List<Throwable> errors, final Map.Entry<Object, List<FrameworkMethod>> entry) {
        for (FrameworkMethod afterMethod : entry.getValue()) {
            try {
                final Object target = entry.getKey();
                afterMethod.invokeExplosively(target);
            } catch (Throwable e) {
                errors.add(e);
            }
        }
    }
}
