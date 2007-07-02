package org.junit.experimental.theories.methods.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersSuppliedBy {

	Class<? extends ParameterSupplier> value();

}
