package org.junit.rules;

import org.junit.Repeat;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RepeatRule implements TestRule {
    private final int runs;

    public RepeatRule() {
        this.runs = 1;
    }

    public RepeatRule(int runs) {
        this.runs = runs;
    }

    public Statement apply(final Statement base, Description description) {
        Repeat annotation = description.getAnnotation(Repeat.class);
        final int iterations = annotation == null ? runs : annotation.value();
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                for (int i = 0; i < iterations; i++) {
                    base.evaluate();
                }
            }
        };
    }
}
