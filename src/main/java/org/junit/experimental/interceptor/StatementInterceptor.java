/**
 * 
 */
package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public interface StatementInterceptor {
	Statement intercept(Statement base, FrameworkMethod method);
}