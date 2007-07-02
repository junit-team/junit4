/**
 * 
 */
package org.junit.experimental.theories.methods.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Theory {
	boolean nullsAccepted() default true;
}