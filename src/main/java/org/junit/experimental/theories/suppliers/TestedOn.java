package org.junit.experimental.theories.suppliers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.experimental.theories.ParametersSuppliedBy;

/**
 * @since 4.4
 */
@ParametersSuppliedBy(TestedOnSupplier.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestedOn {
	int[] ints();
}
