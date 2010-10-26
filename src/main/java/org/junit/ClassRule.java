package org.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.rules.BisectionRule;

/**
 * TODO: fix
 * 
 * Annotates static fields that contain rules. Such a field must be public,
 * static, and a subtype of {@link BisectionRule}.  This rule wraps the entire
 * execution of a class's methods.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassRule {
}