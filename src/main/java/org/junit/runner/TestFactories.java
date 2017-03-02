package org.junit.runner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runners.ReflectionTestFactory;
import org.junit.runners.TestFactory;

/**
 * This annotation allows override of default {@link TestFactory} used by
 * {@link org.junit.runners.CustomizableJUnit4ClassRunner}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface TestFactories {

	Class<? extends TestFactory>[] values() default { ReflectionTestFactory.class };
}
