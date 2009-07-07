package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Verifier is a base class for Rules like ErrorCollector, which can turn
 * otherwise passing test methods into failing tests if a verification check is
 * failed
 * 
 * <pre>
 *     public static class ErrorLogVerifier() {
 *        private ErrorLog errorLog = new ErrorLog();
 *     
 *        &#064;Rule
 *        public MethodRule verifier = new Verifier() {
 *           &#064;Override public void verify() {
 *              assertTrue(errorLog.isEmpty());
 *           }
 *        }
 *        
 *        &#064;Test public void testThatMightWriteErrorLog() {
 *           // ...
 *        }
 *     }
 * </pre>
 */
public class Verifier implements MethodRule {
	public Statement apply(final Statement base, FrameworkMethod method,
			Object target) {
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
