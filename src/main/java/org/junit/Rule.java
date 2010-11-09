package org.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.rules.MethodRule;

/**
 * Annotates fields that contain rules. Such a field must be public, not
 * static, and a subtype of {@link TestRule}. For more information,
 * see {@link TestRule}
 * 
 * Note: for backwards compatibility, this annotation may also mark
 * fields of type {@link MethodRule}, which will be honored.  However,
 * this is a deprecated interface and feature
 */
@SuppressWarnings("deprecation")
@Retention(RetentionPolicy.RUNTIME)
public @interface Rule {

}