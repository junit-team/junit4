package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Sometimes several tests need to share computationally expensive setup
 * (like logging into a database). While this can compromise the independence of 
 * tests, sometimes it is a necessary optimization. Annotating a <code>public static void</code> no-arg method
 * with <code>@BeforeClass</code> causes it to be run once before any of 
 * the test methods in the class. The <code>@BeforeClass</code> methods of superclasses
 * will be run before those the current class.</p>
 * 
 * For example:
 * <pre>
 * public class Example {
 *    &#064;BeforeClass public static void onlyOnce() {
 *       ...
 *    }
 *    &#064;Test public void one() {
 *       ...
 *    }
 *    &#064;Test public void two() {
 *       ...
 *    }
 * }
 * </pre>
 * @see org.junit.AfterClass
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeClass {
}
