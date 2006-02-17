package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sometimes you want to temporarily disable a test. Methods annotated with {@link org.junit.Test}
 * that are also annotated with <code>@Ignore</code> will not be executed as tests. Native JUnit 4 test runners
 * should report the number of ignored tests along with the number of tests that ran and the
 * number of tests that failed.
 * <p>
 * For example:<br>
 * <code>
 * &nbsp;&nbsp;@Ignore @Test public void something() { ...<br>
 * </code>
 * <code>@Ignore</code> takes an optional default parameter if you want to record why a test is being ignored:<br>
 * <code>
 * &nbsp;&nbsp;@Ignore("not ready yet") @Test public void something() { ...<br>
 * </code>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Ignore {
	/**
	 * The optional reason why the test is ignored.
	 */
	String value() default ""; 
}
