package org.junit.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code JUnitSelfTest} annotation is used to tell the
 * {@link JUnitSelfTestRunner}, that an inner classes is a test.
 * 
 * @see JUnitSelfTestRunner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface JUnitSelfTest {

}
