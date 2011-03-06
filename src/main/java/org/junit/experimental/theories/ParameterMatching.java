package org.junit.experimental.theories;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static org.junit.experimental.theories.Reflector.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
public @interface ParameterMatching {
    Reflector value() default WITHOUT_GENERICS;
}
