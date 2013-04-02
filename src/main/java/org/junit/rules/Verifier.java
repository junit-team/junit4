package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Verifier is a base class for Rules like ErrorCollector, which can turn
 * otherwise passing test methods into failing tests if a verification check is
 * failed
 *
 * <pre>
 *     public static class ErrorLogVerifier {
 *        private ErrorLog errorLog = new ErrorLog();
 *
 *        &#064;Rule
 *        public Verifier verifier = new Verifier() {
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
 *
 * @since 4.7
 */
public abstract class Verifier implements TestRule {
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                verify();
            }
        };
    }

    /**
     * Override this to add verification logic. Overrides should throw an
     * exception to indicate that verification failed.
     */
    protected void verify() throws Throwable {
    }
}
