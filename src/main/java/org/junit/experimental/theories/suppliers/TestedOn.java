package org.junit.experimental.theories.suppliers;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.experimental.theories.ParametersSuppliedBy;

/**
 * Annotating a {@link org.junit.experimental.theories.Theory Theory} method int
 * parameter with &#064;TestedOn causes it to be supplied with values from the
 * ints array given when run as a theory by the
 * {@link org.junit.experimental.theories.Theories Theories} runner. For
 * example, the below method would be called three times by the Theories runner,
 * once with each of the int parameters specified.
 * 
 * <pre>
 * &#064;Theory
 * public void shouldPassForSomeInts(&#064;TestedOn(ints={1, 2, 3}) int param) {
 *     ...
 * }
 * </pre>
 */
@ParametersSuppliedBy(TestedOnSupplier.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(PARAMETER)
public @interface TestedOn {
    int[] ints();
}
