package org.junit.experimental.runners.customizable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation allows override of default {@link TestFactory} used by
 * {@link org.junit.experimental.runners.customizable.CustomizableJUnit4ClassRunner}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface TestFactories {

	Class<? extends TestFactory>[] value() default { ReflectionTestFactory.class };
}
