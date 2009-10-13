package org.junit.runner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When a class is annotated with <code>&#064;RunWith</code> or extends a class annotated 
 * with <code>&#064;RunWith</code>, JUnit will invoke the class it references to run the 
 * tests in that class instead of the runner built into JUnit. We added this feature late 
 * in development. While it seems powerful we expect the runner API to change as we learn 
 * how people really use it. Some of the classes that are currently internal will likely 
 * be refined and become public.
 * 
 * For example, suites in JUnit 4 are built using RunWith, and a custom runner named Suite:
 * 
 * <pre>
 * &#064;RunWith(Suite.class)
 * &#064;SuiteClasses({ATest.class, BTest.class, CTest.class})
 * public class ABCSuite {
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RunWith {
	/**
	 * @return a Runner class (must have a constructor that takes a single Class to run)
	 */
	Class<? extends Runner> value();
}
