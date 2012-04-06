package org.junit.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code ExpectedEvents} annotation is used to tell the
 * {@link JUnitSelfTestRunner}, which field provides the test specification.
 * 
 * @see JUnitSelfTestRunner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ExpectedEvents {

}
