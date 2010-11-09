package org.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.rules.TestRule;

/**
 * TODO: expand (see TestRule for help)
 * 
 * Annotates static fields that contain rules. Such a field must be public,
 * static, and a subtype of {@link TestRule}.  This rule wraps the entire
 * execution of a class's methods.
 */
// TODO: note that Class-level statements never fail
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassRule {
}