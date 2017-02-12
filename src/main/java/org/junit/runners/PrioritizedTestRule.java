package org.junit.runners;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

class PrioritizedTestRule extends PrioritizedRule implements TestRule {
    protected final TestRule delegate;

    PrioritizedTestRule(TestRule delegate, int priority) {
        super(priority);
        this.delegate = delegate;
    }

    public Statement apply(Statement base, Description description) {
        return delegate.apply(base, description);
    }
    
    @Override
    public Statement applyAtMethodLevel(
            Statement base, Description description, FrameworkMethod method, Object target) {
        return delegate.apply(base, description);
    }

    @Override
    Object getDelegate() {
        return delegate;
    }
}
