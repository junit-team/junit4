package org.junit.runner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.manipulation.Ordering;
import org.junit.validator.ValidateWith;

/**
 * When a test class is annotated with <code>&#064;OrderWith</code> or extends a class annotated
 * with <code>&#064;OrderWith</code>, JUnit will order the tests in the test class (and child
 * test classes, if any) using the ordering defined by the {@link Ordering} class.
 *
 * @since 4.13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@ValidateWith(OrderWithValidator.class)
public @interface OrderWith {
    /**
     * Gets a class that extends {@link Ordering}. The class must have a public no-arg constructor.
     */
    Class<? extends Ordering.Factory> value();
}
