package org.junit.fixtures;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestRule;

/**
 * A test fixture adds behavior before and after a test method, or set of test methods.
 * It may perform necessary setup or cleanup for tests, or it may verify that some
 * condition holds true after a test (or set of tests) runs witout throwing an
 * exception.
 *
 * Test fixtures are a simpler replacement for {@link TestRule}, and can do everything
 * that could be done previously with methods annotated with {@link org.junit.Before},
 * {@link org.junit.After}, {@link org.junit.BeforeClass}, or {@link org.junit.AfterClass},
 * but they allow a single object to encapsulate these behaviors, and therefore are
 * reusable between classes and projects. They are also composeable.
 *
 * The default JUnit test runners for suites and individual test cases will install
 * {@code TestFixture} instances that are found via non-static methods and fields
 * annotated with {@link org.junit.Rule} (in which case setup and tear-down is on a
 * per-method level) as well as static methods and fields (in which case they work on a
 * per-class or per-suite level). In all cases, the fields or methods should return
 * an instance of {@link TestFixture}. See Javadoc for {@link Rule} and {@link ClassRule}
 * for more information.
 *
 * Multiple test fixtures can be applied to a test or suite execution.
 *
 * To make writing test fixtures easier, you can use {@link AbstractTestFixture}.
 *
 * To see how test fixtures can be useful, see these provided fixturs.
 *
 * <ul>
 *   <li>{@link TemporaryDirectory}: create fresh files, and delete them after test runs</li>
 * </ul>
 *
 * @since 4.13
 */

public interface TestFixture {

    /**
     * Initializes the test fixture.
     */
    void initialize(FixtureContext context) throws Exception;
}
