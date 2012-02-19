package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runners.MethodSorters;

/**
 * SortMethodsWith allows the user to choose the order of execution of the methods within a test class.
 * <br/>
 * <br/>
 * The default order of execution of JUnit tests within a class is deterministic but not predictable.
 * Before 4.11, the behaviour was to run the test methods in byte code order, which pre-Java 7 was mostly predictable.
 * Java 7 (and some previous versions), does not guaranteee the order of execution, which can change from run to run,
 * so a deterministic sort was introduced.
 * <br/>
 * As a rule, test method execution should be independent of one another. However, there may be a number of dependent tests
 * either through error or by design. This class allows the user to specify the order of execution of test methods.
 * <br/>
 * There are four possibilities:
 * <ul>
 * <li>MethodSorters.DEFAULT: the default value, deterministic, but not predictable</li>
 * <li>MethodSorters.JVM: the order in which the tests are returned by the JVM, i.e. there is no sorting done</li>
 * <li>MethodSorters.NAME_ASC: sorted in order of method name, ascending</li>
 * <li>MethodSorters.NAME_DESC: sorter in order of method name, descending</li>
 * </ul>
 * 
 * Here is an example:
 * 
 * <pre>
 * &#064;SortMethodsWith(MethodSorters.NAME_ASC)
 * public class MyTest {
 * }
 * </pre>
 * 
 * @see org.junit.runners.MethodSorters
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SortMethodsWith {
	/**
	 * Optionally specify <code>sorter</code> to have the methods executed in a particular order
	 */
	MethodSorters value() default MethodSorters.DEFAULT;
}
