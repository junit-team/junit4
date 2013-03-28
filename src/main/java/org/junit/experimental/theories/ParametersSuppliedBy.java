package org.junit.experimental.theories;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ANNOTATION_TYPE, PARAMETER})
public @interface ParametersSuppliedBy {

    Class<? extends ParameterSupplier> value();

}
