/**
 * 
 */
package org.junit.runner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// TODO: (Aug 6, 2007 4:06:58 PM) Are package contents what we want?

@Retention(RetentionPolicy.RUNTIME)
public @interface Category {
	Class<?> value();
}