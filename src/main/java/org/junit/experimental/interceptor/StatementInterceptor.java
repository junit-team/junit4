package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public interface StatementInterceptor {
	// TODO (Jul 1, 2009 1:43:11 PM): add documentation to
	// BlockJUnit4ClassRunner
	Statement intercept(Statement base, FrameworkMethod method);
}