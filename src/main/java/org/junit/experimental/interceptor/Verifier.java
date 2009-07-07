package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class Verifier implements StatementInterceptor {
	public Statement intercept(final Statement base, FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
				verify();
			}
		};
	}

	protected void verify() throws Throwable {
	}
}
