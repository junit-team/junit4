package org.junit.experimental.theories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.experimental.theories.internal.SpecificDataPointsSupplier;

/**
 * Annotating a parameter of a {@link org.junit.experimental.theories.Theory
 * &#064;Theory} method with <code>&#064;FromDataPoints</code> will limit the
 * datapoints considered as potential values for that parameter to just the
 * {@link org.junit.experimental.theories.DataPoints DataPoints} with the given
 * name. DataPoint names can be given as the value parameter of the
 * &#064;DataPoints annotation.
 * <p>
 * DataPoints without names will not be considered as values for any parameters
 * annotated with &#064;FromDataPoints.
 * <pre>
 * &#064;DataPoints
 * public static String[] unnamed = new String[] { ... };
 * 
 * &#064;DataPoints("regexes")
 * public static String[] regexStrings = new String[] { ... };
 * 
 * &#064;DataPoints({"forMatching", "alphanumeric"})
 * public static String[] testStrings = new String[] { ... }; 
 * 
 * &#064;Theory
 * public void stringTheory(String param) {
 *     // This will be called with every value in 'regexStrings',
 *     // 'testStrings' and 'unnamed'.
 * }
 * 
 * &#064;Theory
 * public void regexTheory(&#064;FromDataPoints("regexes") String regex,
 *                         &#064;FromDataPoints("forMatching") String value) {
 *     // This will be called with only the values in 'regexStrings' as 
 *     // regex, only the values in 'testStrings' as value, and none 
 *     // of the values in 'unnamed'.
 * }
 * </pre>
 * 
 * @see org.junit.experimental.theories.Theory
 * @see org.junit.experimental.theories.DataPoint
 * @see org.junit.experimental.theories.DataPoints
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ParametersSuppliedBy(SpecificDataPointsSupplier.class)
public @interface FromDataPoints {
    String value();
}
