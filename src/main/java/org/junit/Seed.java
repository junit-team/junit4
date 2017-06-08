package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class allows the user to seed the random number generator
 * used when executing tests in random order.
 * 
 * <p>Seeding the random number generator in this way allows tests that
 * were executed in random order (using MethodSorters.RANDOM) 
 * to be re-run in the same order on a subsequent test.
 * 
 * <p>For example, suppose one has a test class like the following:
 * 
 * <pre>
 * &#064;FixMethodOrder(MethodSorters.RANDOM) 
 * public class MyTest {
 * }
 * </pre>
 * 
 * <p>Now suppose the tests are run. At the beginning of the test run,
 * JUnit will print the random number generator seed it used to run the
 * tests. Now suppose one of the tests fails and it is desired to run the
 * tests in the same order as the previous run. The user then adds a Seed
 * annotation to the test class to specify the same random number seed
 * used in the last run (0xab84b37cb8abc82fL in this example).
 * <pre>
 * &#064;FixMethodOrder(MethodSorters.RANDOM)
 * &#064;Seed(0xab84b37cb8abc82fL)
 * </pre>
 * 
 * @see org.junit.FixMethodOrder
 * @see org.junit.runners.MethodSorters
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Seed {
    /**
     * Seed for random number generator for use in applying a random method
     * sort order.
     */
    long value();
}

