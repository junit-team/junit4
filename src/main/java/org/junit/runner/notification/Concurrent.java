// Copyright 2013 Google Inc. All Rights Reserved.

package org.junit.runner.notification;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a {@link RunListener} that can have its methods called
 * concurrently. This implies that the class is thread-safe (i.e. no set of
 * listener calls can put the listener into an invalid state, even if those
 * listener calls are being made by multiple threads without synchronization.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Concurrent {
}
