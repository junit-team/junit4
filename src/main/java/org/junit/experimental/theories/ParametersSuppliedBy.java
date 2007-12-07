package org.junit.experimental.theories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersSuppliedBy {

	Class<? extends ParameterSupplier> value();

}
