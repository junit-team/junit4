package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you allocate external resources in a {@link org.junit.Before} method you need to release them
 * after the test runs. Annotating a <code>public void</code> method
 * with <code>@After</code> causes that method to be run after the {@link org.junit.Test} method. All <code>@After</code>
 * methods are guaranteed to run even if a {@link org.junit.Before} or {@link org.junit.Test} method throws an 
 * exception. The <code>@After</code> methods declared in superclasses will be run after those of the current
 * class.
 * <p>
 * Here is a simple example:<br>
* <code>
 * public class Example {<br>
 * &nbsp;&nbsp;File output;<br>
 * &nbsp;&nbsp;@Before public void createOutputFile() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;output= new File(...);<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@Test public void something() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;@After public void deleteOutputFile() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;output.delete();<br>
 * &nbsp;&nbsp;}<br>
 * }<br>
 * </code>
 * 
 * @see org.junit.Before
 * @see org.junit.Test
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface After {
}

