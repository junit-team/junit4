package org.junit.rules;

import org.junit.Rule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * A MethodRule is an alteration in how a test method is run and reported.
 * Multiple {@link MethodRule}s can be applied to a test method. The
 * {@link Statement} that executes the method is passed to each annotated
 * {@link Rule} in turn, and each may return a substitute or modified
 * {@link Statement}, which is passed to the next {@link Rule}, if any. For
 * an example of how this can be useful, see {@link TestWatchman}.
 *
 * <p>Note that {@link MethodRule} has been replaced by {@link TestRule},
 * which has the added benefit of supporting class rules.
 *
 * @since 4.7
 */
public interface MethodRule {
    /**
     * Modifies the method-running {@link Statement} to implement an additional
     * test-running rule.
     *
     * @param base The {@link Statement} to be modified
     * @param method The method to be run
     * @param target The object on which the method will be run.
     * @return a new statement, which may be the same as {@code base},
     *         a wrapper around {@code base}, or a completely new Statement.
     */
    Statement apply(Statement base, FrameworkMethod method, Object target);
}
