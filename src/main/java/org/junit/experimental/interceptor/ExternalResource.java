package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ExternalResource implements StatementInterceptor {
	public final Statement intercept(final Statement base, FrameworkMethod method) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before();
				try {
					base.evaluate();
				} finally {
					after();
				}
			}
		};
	}

	protected void before() throws Throwable {
		// do nothing
	}

	protected void after() {
		// do nothing
	}
}
