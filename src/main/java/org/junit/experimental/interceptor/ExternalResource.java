package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ExternalResource implements StatementInterceptor {
	public final Statement intercept(final Statement base, FrameworkMethod method) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				// TODO (Jun 3, 2009 11:49:23 PM): should we replicate
				// @Before/@After semantics?
				// 1. after() run even if before() fails.
				// 2. exception in after() _adds_ to failure in base.
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
