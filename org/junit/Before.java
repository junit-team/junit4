package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When writing tests, it is common to find that several tests need similar 
 * objects created before they can run. Annotating a <code>public void</code> method
 * with <code>@Before</code> causes that method to be run before the {@link org.junit.Test} method.
 * The <code>@Before</code> methods of superclasses will be run before those of the current class.
 * <p>
 * Here is a simple example:
* <code>
 * public class Example {<br>
 * &nbsp;&nbsp;List empty;<br>
 * &nbsp;&nbsp;@Before public static void initialize() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;empty= new ArrayList();<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@Test public void size() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@Test public void remove() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * }<br>
 * </code>
 * 
 * @see org.junit.BeforeClass
 * @see org.junit.After
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {
}

