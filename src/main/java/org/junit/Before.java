package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When writing tests, it is common to find that several tests need similar
 * objects created before they can run. Annotating a <code>public void</code> method
 * with <code>&#064;Before</code> causes that method to be run before the {@link org.junit.Test} method.
 * The <code>&#064;Before</code> methods of superclasses will be run before those of the current class,
 * unless they are overridden in the current class. No other ordering is defined.
 * <p>
 * Here is a simple example:
 * <pre>
 * public class Example {
 *    List empty;
 *    &#064;Before public void initialize() {
 *       empty= new ArrayList();
 *    }
 *    &#064;Test public void size() {
 *       ...
 *    }
 *    &#064;Test public void remove() {
 *       ...
 *    }
 * }
 * </pre>
 *
 * @see org.junit.BeforeClass
 * @see org.junit.After
 * @since 4.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {
}

