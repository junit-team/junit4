package org.junit.rules;

import org.junit.Rule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * [[MethodRule has been deprecated, and all uses will be changed to TestRule, 
 * so this javadoc will become more truthy with time.]]
 * 
 * A TestRule is an alteration in how a test method is run and reported.
 * Multiple {@link TestRule}s can be applied to a test method. The
 * {@link Statement} that executes the method is passed to each annotated
 * {@link Rule} in turn, and each may return a substitute or modified
 * {@link Statement}, which is passed to the next {@link Rule}, if any. For
 * examples of how this can be useful, see these provided TestRules,
 * or write your own:
 * 
 * <ul>
 *   <li>{@link ErrorCollector}: collect multiple errors in one test method</li>
 *   <li>{@link ExpectedException}: make flexible assertions about thrown exceptions</li>
 *   <li>{@link ExternalResource}: start and stop a server, for example</li>
 *   <li>{@link TemporaryFolder}: create fresh files, and delete after test</li>
 *   <li>{@link TestName}: remember the test name for use during the method</li>
 *   <li>{@link TestWatchman}: add logic at events during method execution</li>
 *   <li>{@link Timeout}: cause test to fail after a set time</li>
 *   <li>{@link Verifier}: fail test if object state ends up incorrect</li>
 * </ul>
 */
public interface TestRule {
	/**
	 * Modifies the method-running {@link Statement} to implement an additional
	 * test-running rule.
	 * 
	 * @param base The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @return a new statement, which may be the same as {@code base},
	 * a wrapper around {@code base}, or a completely new Statement.
	 */
	Statement apply(Statement base, Description description);
}
