package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class TestWatchman implements StatementInterceptor {
	public Statement intercept(final Statement base,
			final FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				starting(method);
				try {
					base.evaluate();
					succeeded(method);
				} catch (Throwable t) {
					failed(t, method);
					throw t;
				} finally {
					finished(method);
				}
			}
		};
	}

	public void succeeded(FrameworkMethod method) {
	}

	public void failed(Throwable e, FrameworkMethod method) {
	}

	public void starting(FrameworkMethod method) {
	}

	public void finished(FrameworkMethod method) {
	}
}
