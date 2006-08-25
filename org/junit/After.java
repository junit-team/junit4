package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>If you allocate external resources in a {@link org.junit.Before} method you need to release them
 * after the test runs. Annotating a <code>public void</code> method
 * with <code>&#064;After</code> causes that method to be run after the {@link org.junit.Test} method. All <code>&#064;After</code>
 * methods are guaranteed to run even if a {@link org.junit.Before} or {@link org.junit.Test} method throws an 
 * exception. The <code>&#064;After</code> methods declared in superclasses will be run after those of the current
 * class.</p>
 * 
 * Here is a simple example:
* <pre>
 * public class Example {
 *    File output;
 *    &#064;Before public void createOutputFile() {
 *          output= new File(...);
 *    }
 *    &#064;Test public void something() {
 *          ...
 *    }
 *    &#064;After public void deleteOutputFile() {
 *          output.delete();
 *    }
 * }
 * </pre>
 * 
 * @see org.junit.Before
 * @see org.junit.Test
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface After {
}

