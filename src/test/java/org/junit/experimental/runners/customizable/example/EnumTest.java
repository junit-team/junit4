package org.junit.experimental.runners.customizable.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnumTest {

	Class<? extends Enum<?>> enumType();

	boolean nullable() default false;
}
