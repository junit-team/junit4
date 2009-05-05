/**
 * 
 */
package org.junit.experimental.interceptor;

import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class Timeout implements StatementInterceptor {
	private final int fMillis;

	public Timeout(int millis) {
		fMillis= millis;
	}

	public Statement intercept(Statement base, FrameworkMethod method) {
		return new FailOnTimeout(base, fMillis);
	}
}