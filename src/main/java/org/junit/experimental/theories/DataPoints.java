package org.junit.experimental.theories;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating an array or iterable-typed field or method with &#064;DataPoints
 * will cause the values in the array or iterable given to be used as potential
 * parameters for theories in that class when run with the
 * {@link org.junit.experimental.theories.Theories Theories} runner.
 * <p>
 * DataPoints will only be considered as potential values for parameters for
 * which their types are assignable. When multiple sets of DataPoints exist with
 * overlapping types more control can be obtained by naming the DataPoints using
 * the value of this annotation, e.g. with
 * <code>&#064;DataPoints({"dataset1", "dataset2"})</code>, and then specifying
 * which named set to consider as potential values for each parameter using the
 * {@link org.junit.experimental.theories.FromDataPoints &#064;FromDataPoints}
 * annotation.
 * <p>
 * Parameters with no specified source (i.e. without &#064;FromDataPoints or
 * other {@link org.junit.experimental.theories.ParametersSuppliedBy
 * &#064;ParameterSuppliedBy} annotations) will use all DataPoints that are
 * assignable to the parameter type as potential values, including named sets of
 * DataPoints.
 * <p>
 * DataPoints methods whose array types aren't assignable from the target
 * parameter type (and so can't possibly return relevant values) will not be
 * called when generating values for that parameter. Iterable-typed datapoints
 * methods must always be called though, as this information is not available
 * here after generic type erasure, so expensive methods returning iterable
 * datapoints are a bad idea.
 * 
 * <pre>
 * &#064;DataPoints
 * public static String[] dataPoints = new String[] { ... };
 * 
 * &#064;DataPoints
 * public static String[] generatedDataPoints() {
 *     return new String[] { ... };
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
@Target({ FIELD, METHOD })
public @interface DataPoints {
    String[] value() default {};

    Class<? extends Throwable>[] ignoredExceptions() default {};
}
