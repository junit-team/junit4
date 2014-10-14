package org.junit;

import org.junit.internal.sorters.MethodSorter;

import java.lang.annotation.*;

/**
 * This class allows the user to choose the order of execution of the methods within a test class.
 *
 * <p>The default order of execution of JUnit tests within a class is deterministic but not predictable.
 * The order of execution is not guaranteed for Java 7 (and some previous versions), and can even change
 * from run to run, so the order of execution was changed to be deterministic (in JUnit 4.11)
 *
 * <p>It is recommended that test methods be written so that they are independent of the order that they are executed.
 * However, there may be a number of dependent tests either through error or by design.
 * This class allows the user to specify the order of execution of test methods.</p>
 *
 * <p>There are three built-in sorters shipped with JUnit: default, jvm and name ascending. You can write your own
 * implementation of {@link org.junit.internal.sorters.DefaultMethodSorter}. You can also use this mechanism to provide random order
 * execution.</p>
 *
 * <pre>
 * &#064;SortWith(DefaultMethodSorter.class)
 * public class MyTest {
 * }
 * </pre>
 *
 * @see org.junit.internal.sorters.DefaultMethodSorter
 * @see org.junit.internal.sorters.NameAscendingMethodSorter
 * @see org.junit.internal.sorters.JvmMethodSorter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface SortWith {
    /**
     * Optionally specify <code>value</code> to have the methods executed in a particular order
     */
    Class<? extends MethodSorter> value();
}
