package org.junit;

import org.junit.function.ThrowingRunnable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>Test</code> annotation tells JUnit that the <code>public void</code> method
 * to which it is attached can be run as a test case. To run the method,
 * JUnit first constructs a fresh instance of the class then invokes the
 * annotated method. Any exceptions thrown by the test will be reported
 * by JUnit as a failure. If no exceptions are thrown, the test is assumed
 * to have succeeded.
 * <p>
 * A simple test looks like this:
 * <pre>
 * public class Example {
 *    <b>&#064;Test</b>
 *    public void method() {
 *       org.junit.Assert.assertTrue( new ArrayList().isEmpty() );
 *    }
 * }
 * </pre>
 * <p>
 * The <code>Test</code> annotation supports two optional parameters for
 * exception testing and for limiting test execution time.
 *
 * <h3>Exception Testing</h3>
 * <p>
 * The parameter <code>expected</code> declares that a test method should throw
 * an exception. If it doesn't throw an exception or if it throws a different exception
 * than the one declared, the test fails. For example, the following test succeeds:
 * <pre>
 *    &#064;Test(<b>expected=IndexOutOfBoundsException.class</b>)
 *    public void outOfBounds() {
 *       new ArrayList&lt;Object&gt;().get(1);
 *    }
 * </pre>
 *
 * Using the parameter <code>expected</code> for exception testing comes with
 * some limitations: only the exception's type can be checked and it is not
 * possible to precisely specify the code that throws the exception. Therefore
 * JUnit 4 has improved its support for exception testing with
 * {@link Assert#assertThrows(Class, ThrowingRunnable)} and the
 * {@link org.junit.rules.ExpectedException ExpectedException} rule.
 * With <code>assertThrows</code> the code that throws the exception can be
 * precisely specified. If the exception's message or one of its properties
 * should be verified, the <code>ExpectedException</code> rule can be used. Further
 * information about exception testing can be found at the
 * <a href="https://github.com/junit-team/junit4/wiki/Exception-testing">JUnit Wiki</a>.
 *
 * <h3>Timeout</h3>
 * <p>
 * The parameter <code>timeout</code> causes a test to fail if it takes
 * longer than a specified amount of clock time (measured in milliseconds). The following test fails:
 * <pre>
 *    &#064;Test(<b>timeout=100</b>)
 *    public void infinity() {
 *       while(true);
 *    }
 * </pre>
 * <b>Warning</b>: while <code>timeout</code> is useful to catch and terminate
 * infinite loops, it should <em>not</em> be considered deterministic. The
 * following test may or may not fail depending on how the operating system
 * schedules threads:
 * <pre>
 *    &#064;Test(<b>timeout=100</b>)
 *    public void sleep100() {
 *       Thread.sleep(100);
 *    }
 * </pre>
 * <b>THREAD SAFETY WARNING:</b> Test methods with a timeout parameter are run in a thread other than the
 * thread which runs the fixture's @Before and @After methods. This may yield different behavior for
 * code that is not thread safe when compared to the same test method without a timeout parameter.
 * <b>Consider using the {@link org.junit.rules.Timeout} rule instead</b>, which ensures a test method is run on the
 * same thread as the fixture's @Before and @After methods.
 *
 * @since 4.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Test {

    /**
     * Default empty exception.
     */
    static class None extends Throwable {
        private static final long serialVersionUID = 1L;

        private None() {
        }
    }

    /**
     * Optionally specify <code>expected</code>, a Throwable, to cause a test method to succeed if
     * and only if an exception of the specified class is thrown by the method. If the Throwable's
     * message or one of its properties should be verified, the
     * {@link org.junit.rules.ExpectedException ExpectedException} rule can be used instead.
     */
    Class<? extends Throwable> expected() default None.class;

    /**
     * Optionally specify <code>timeout</code> in milliseconds to cause a test method to fail if it
     * takes longer than that number of milliseconds.
     * <p>
     * <b>THREAD SAFETY WARNING:</b> Test methods with a timeout parameter are run in a thread other than the
     * thread which runs the fixture's @Before and @After methods. This may yield different behavior for
     * code that is not thread safe when compared to the same test method without a timeout parameter.
     * <b>Consider using the {@link org.junit.rules.Timeout} rule instead</b>, which ensures a test method is run on the
     * same thread as the fixture's @Before and @After methods.
     * </p>
     */
    long timeout() default 0L;
}
