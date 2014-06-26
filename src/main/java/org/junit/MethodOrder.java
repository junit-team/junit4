package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodOrder {
    /**
     * Optionally specify <code>value</code> to have the methods executed in a
     * particular order
     */
    int value() default 0;
}
