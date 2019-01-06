package org.junit.experimental.theories;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a {@link org.junit.experimental.theories.Theory Theory} method
 * parameter with &#064;ParametersSuppliedBy causes it to be supplied with
 * values from the named
 * {@link org.junit.experimental.theories.ParameterSupplier ParameterSupplier}
 * when run as a theory by the {@link org.junit.experimental.theories.Theories
 * Theories} runner.
 * 
 * In addition, annotations themselves can be annotated with
 * &#064;ParametersSuppliedBy, and then used similarly. ParameterSuppliedBy
 * annotations on parameters are detected by searching up this hierarchy such
 * that these act as syntactic sugar, making:
 * 
 * <pre>
 * &#064;ParametersSuppliedBy(Supplier.class)
 * public &#064;interface SpecialParameter { }
 * 
 * &#064;Theory
 * public void theoryMethod(&#064;SpecialParameter String param) {
 *   ...
 * }
 * </pre>
 * 
 * equivalent to:
 * 
 * <pre>
 * &#064;Theory
 * public void theoryMethod(&#064;ParametersSuppliedBy(Supplier.class) String param) {
 *   ...
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ANNOTATION_TYPE, PARAMETER })
public @interface ParametersSuppliedBy {

    Class<? extends ParameterSupplier> value();

}
