package org.junit.experimental.theories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.experimental.theories.internal.SpecificDataPointsSupplier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ParametersSuppliedBy(SpecificDataPointsSupplier.class)
public @interface FromDataPoints {
    String value();
}
