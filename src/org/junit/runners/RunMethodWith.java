package org.junit.runners;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RunMethodWith {
	Class<? extends MethodRunner> value();
}
