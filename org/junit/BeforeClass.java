package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sometimes several tests need to share computationally expensive setup
 * (like logging into a database). While this can compromise the independence of 
 * tests, sometimes it is a necessary optimization. Annotating a <code>public static void</code> no-arg method
 * with <code>@BeforeClass</code> causes it to be run once before any of 
 * the test methods in the class. The <code>@BeforeClass</code> methods of superclasses
 * will be run before those the current class.
 * <p>
 * For example:<br>
 * 
 * <code>
 * public class Example {<br>
 * &nbsp;&nbsp;@BeforeClass public static void onlyOnce() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@Test public void one() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@Test public void two() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * }<br>
 * </code>
 * @see org.junit.AfterClass
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeClass {
}
