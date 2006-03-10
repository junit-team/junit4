package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you allocate expensive external resources in a {@link org.junit.BeforeClass} method you need to release them
 * after all the tests in the class have run. Annotating a <code>public static void</code> method
 * with <code>@AfterClass</code> causes that method to be run after all the tests in the class have been run. All <code>@AfterClass</code>
 * methods are guaranteed to run even if a {@link org.junit.BeforeClass} method throws an 
 * exception. The <code>@AfterClass</code> methods declared in superclasses will be run after those of the current
 * class.
 * <p>
 * Here is a simple example:<br>
* <code>
 * public class Example {<br>
 * &nbsp;&nbsp;DatabaseConnection database;<br>
 * &nbsp;&nbsp;@BeforeClass public void login() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;database= ...;<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@Test public void something() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@Test public void somethingElse() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@AfterClass public void logout() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;database.logout();<br>
 * &nbsp;&nbsp;}<br>
 * }<br>
 * </code>
 * 
 * @see org.junit.BeforeClass
 * @see org.junit.Test
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterClass {
}
