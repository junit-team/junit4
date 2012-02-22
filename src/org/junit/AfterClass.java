package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>If you allocate expensive external resources in a {@link org.junit.BeforeClass} method you need to release them
 * after all the tests in the class have run. Annotating a <code>public static void</code> method
 * with <code>&#064;AfterClass</code> causes that method to be run after all the tests in the class have been run. All <code>&#064;AfterClass</code>
 * methods are guaranteed to run even if a {@link org.junit.BeforeClass} method throws an 
 * exception. The <code>&#064;AfterClass</code> methods declared in superclasses will be run after those of the current
 * class.</p>
 * 
 * Here is a simple example:
* <pre>
 * public class Example {
 *    private static DatabaseConnection database;
 *    &#064;BeforeClass public static void login() {
 *          database= ...;
 *    }
 *    &#064;Test public void something() {
 *          ...
 *    }
 *    &#064;Test public void somethingElse() {
 *          ...
 *    }
 *    &#064;AfterClass public static void logout() {
 *          database.logout();
 *    }
 * }
 * </pre>
 * 
 * @see org.junit.BeforeClass
 * @see org.junit.Test
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterClass {
}
