package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A TestRule is an alteration in how a test method, or set of test methods,
 * is run and reported.  A {@link TestRule} may add additional checks that cause
 * a test that would otherwise fail to pass, or it may perform necessary setup or
 * cleanup for tests, or it may observe test execution to report it elsewhere.
 * {@link TestRule}s can do everything that could be done previously with
 * methods annotated with {@link org.junit.Before}, 
 * {@link org.junit.After}, {@link org.junit.BeforeClass}, or 
 * {@link org.junit.AfterClass}, but they are more powerful, and more easily 
 * shared
 * between projects and classes.
 * 
 * The default JUnit test runners for suites and
 * individual test cases recognize {@link TestRule}s introduced in two different
 * ways.  {@link org.junit.Rule} annotates method-level 
 * {@link TestRule}s, and {@link org.junit.ClassRule} 
 * annotates class-level {@link TestRule}s.  See Javadoc for those annotations
 * for more information.
 *
 * Multiple {@link TestRule}s can be applied to a test or suite execution. The
 * {@link Statement} that executes the method or suite is passed to each annotated
 * {@link org.junit.Rule} in turn, and each may return a substitute or modified
 * {@link Statement}, which is passed to the next {@link org.junit.Rule}, if any. For
 * examples of how this can be useful, see these provided TestRules,
 * or write your own:
 * 
 * <ul>
 *   <li>{@link ErrorCollector}: collect multiple errors in one test method</li>
 *   <li>{@link ExpectedException}: make flexible assertions about thrown exceptions</li>
 *   <li>{@link ExternalResource}: start and stop a server, for example</li>
 *   <li>{@link TemporaryFolder}: create fresh files, and delete after test</li>
 *   <li>{@link TestName}: remember the test name for use during the method</li>
 *   <li>{@link TestWatcher}: add logic at events during method execution</li>
 *   <li>{@link Timeout}: cause test to fail after a set time</li>
 *   <li>{@link Verifier}: fail test if object state ends up incorrect</li>
 * </ul>
 */
public interface TestRule {
	/**
	 * Modifies the method-running {@link Statement} to implement this
	 * test-running rule.
	 * 
	 * @param base The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @return a new statement, which may be the same as {@code base},
	 * a wrapper around {@code base}, or a completely new Statement.
	 */
	Statement apply(Statement base, Description description);
}
