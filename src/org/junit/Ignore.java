package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Sometimes you want to temporarily disable a test or a group of tests. Methods annotated with 
 * {@link org.junit.Test} that are also annotated with <code>&#064;Ignore</code> will not be executed as tests.
 * Also, you can annotate a class containing test methods with <code>&#064;Ignore</code> and none of the containing 
 * tests will be executed. Native JUnit 4 test runners should report the number of ignored tests along with the 
 * number of tests that ran and the number of tests that failed.</p>
 * 
 * For example:
 * <pre>
 *    &#064;Ignore &#064;Test public void something() { ...
 * </pre>
 * &#064;Ignore takes an optional default parameter if you want to record why a test is being ignored:<br/>
 * <pre>
 *    &#064;Ignore("not ready yet") &#064;Test public void something() { ...
 * </pre>
 * &#064;Ignore can also be applied to the test class:<br/>
 * <pre>
 *		&#064;Ignore public class IgnoreMe {
 *			&#064;Test public void test1() { ... }
 *			&#064;Test public void test2() { ... }
 *		}
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Ignore {
	/**
	 * The optional reason why the test is ignored.
	 */
	String value() default ""; 
}
