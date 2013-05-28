package org.junit.experimental.validator;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Allows for an {@link AnnotationValidator} to be attached to an annotation.
 *
 * When attached to an annotation, the validator will be instantiated and invoked
 * by the {@link org.junit.runners.ParentRunner}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Validator {
    Class<? extends AnnotationValidator> value()[];
}
