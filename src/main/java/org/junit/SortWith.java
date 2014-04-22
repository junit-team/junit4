package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

import org.junit.runners.Sorters;

/**
 * The <code>SortWith</code> Annotation that tells JUnit to execute test methods 
 * or test classes defined in a test suite in a order specified by users.
 * It also supports randomness of test by designating a random sorting method 
 * to either a test class or a test suite. 
 *
 * <p>
 * A sortable test class with its test methods executed in name ascending order looks like:
 * <pre>
 * 
 * <b>&#064;RunWith(JUnit4.class)</b>
 * <b>&#064;SortWith(Sorters.NAME_ASCENDING)</b>
 * public class Example {
 *    <b>&#064;Test</b>
 *    public void testMethod1() {
 *       
 *    }
 *    
 *    <b>&#064;Test</b>
 *    public void testMethod2() {
 *     
 *    }
 * }
 * </pre>
 * <p>
 *
 * <p>
 * A sortable test suite with its test classes executed in name ascending order looks like:
 * <pre>
 * 
 * <b>&#064;RunWith(Suite.class)</b>
 * <b>&#064;Suite.SuiteClasses({ ClassA.class, ClassB.class })</b>
 * <b>&#064;SortWith(Sorters.NAME_ASCENDING)</b>
 * public class Example {
 * 
 * }
 * </pre>
 * <p>
 * 
 * The <code>SortWith</code> Annotation takes one input parameter, see {@link org.junit.runner.manipulation.Sorter}.
 * The parameter declares which sorting method JUnit should adopt when
 * starts running tests. If no sorting method is specified for the 
 * Annotation, then by default the default sorting method which compares
 * hash codes of names of the two methods or names of two test classes is
 * used. 
 *
 * Be aware that using {@link org.junit.runner.Request#sortWith(Comparator)} to manually specify a sorting
 * method for JUnit overrides all the declarative sorting methods specified through <code>SortWith</code> Annotation.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SortWith {

    Sorters value() default Sorters.DEFAULT;
   
}
