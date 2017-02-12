package org.junit.runners;

import java.util.Iterator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

abstract class PrioritizedRule implements Comparable<PrioritizedRule> {
    private final int priority;

    protected PrioritizedRule(int priority) {
        this.priority = priority;
    }

    public int compareTo(PrioritizedRule other) {
        return priority < other.priority ? -1 : (priority == other.priority ? 0 : 1);
    }

    @Override
    public final boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass() != getClass()) {
            return false;
        }
        return getDelegate().equals(((PrioritizedRule) other).getDelegate());
    }

    @Override
    public final int hashCode() {
        return getDelegate().hashCode();
    }

    abstract Object getDelegate();

    abstract Statement applyAtMethodLevel(
            Statement base, Description description, FrameworkMethod method, Object target);

    @SuppressWarnings("unchecked")
    static <T extends PrioritizedRule> void extract(
            List<?> from, Class<T> clazz, List<T> prioritizedRules) {
        Iterator<?> iterator = from.iterator();
        while (iterator.hasNext()) {
            Object each = iterator.next();
            if (clazz.isInstance(each)) {
                prioritizedRules.add((T) each);
                iterator.remove();
            }
        }
    }
}
