package org.junit.experimental.theories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @since 4.4
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Theory {
	boolean nullsAccepted() default true;
}