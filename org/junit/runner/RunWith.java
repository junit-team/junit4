package org.junit.runner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RunWith {
	/**
	 * @return a Runner class (must have a constructor that takes a single Class to run)
	 */
	Class<? extends Runner> value();
}
