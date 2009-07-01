package org.junit.experimental.interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates fields that contain Interceptors. Such a field must be public, not
 * static, and a subtype of {@link StatementInterceptor}. For more information,
 * see {@link StatementInterceptor}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {

}