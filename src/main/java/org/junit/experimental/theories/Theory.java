package org.junit.experimental.theories;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks test methods that should be read as theories by the {@link org.junit.experimental.theories.Theories Theories} runner.
 *
 * @see org.junit.experimental.theories.Theories
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface Theory {
    boolean nullsAccepted() default true;
}