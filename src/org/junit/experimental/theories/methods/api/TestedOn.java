package org.junit.experimental.theories.methods.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@ParametersSuppliedBy(TestedOnSupplier.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestedOn {
	int[] ints();
}
