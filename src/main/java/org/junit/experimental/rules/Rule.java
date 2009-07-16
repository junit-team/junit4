package org.junit.experimental.rules;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates fields that contain rules. Such a field must be public, not
 * static, and a subtype of {@link MethodRule}. For more information,
 * see {@link MethodRule}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Rule {

}