package org.junit.experimental.theories;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating an field or method with &#064;DataPoint will cause the field value
 * or the value returned by the method to be used as a potential parameter for
 * theories in that class, when run with the
 * {@link org.junit.experimental.theories.Theories Theories} runner.
 * <p>
 * DataPoints will only be considered as potential values for parameters for
 * which their types are assignable. When multiple sets of DataPoints exist with
 * overlapping types more control can be obtained by naming the DataPoints using
 * the value of this annotation, e.g. with
 * <code>&#064;DataPoint({"dataset1", "dataset2"})</code>, and then specifying
 * which named set to consider as potential values for each parameter using the
 * {@link org.junit.experimental.theories.FromDataPoints &#064;FromDataPoints}
 * annotation.
 * <p>
 * Parameters with no specified source (i.e. without &#064;FromDataPoints or
 * other {@link org.junit.experimental.theories.ParametersSuppliedBy
 * &#064;ParameterSuppliedBy} annotations) will use all DataPoints that are
 * assignable to the parameter type as potential values, including named sets of
 * DataPoints.
 * 
 * <pre>
 * &#064;DataPoint
 * public static String dataPoint = "value";
 * 
 * &#064;DataPoint("generated")
 * public static String generatedDataPoint() {
 *     return "generated value";
 * }
 * 
 * &#064;Theory
 * public void theoryMethod(String param) {
 *     ...
 * }
 * </pre>
 * 
 * @see org.junit.experimental.theories.Theories
 * @see org.junit.experimental.theories.Theory
 * @see org.junit.experimental.theories.DataPoint
 * @see org.junit.experimental.theories.FromDataPoints
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD})
public @interface DataPoint {
    String[] value() default {};
}
