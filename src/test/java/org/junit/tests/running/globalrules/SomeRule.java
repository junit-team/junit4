package org.junit.tests.running.globalrules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Helper class for {@link BlockJunit4ClassRunnerGlobalRulesTest}
 */
public class SomeRule implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {

        return new SomeStatement(base);
    }

    public class SomeStatement extends Statement {

        private Statement base;

        public SomeStatement(Statement base) {

            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {

            // set property to allow test to know about rule execution
            System.setProperty("junit.globalrules.somerule.executed", "true");

            // delegate to base
            base.evaluate();
        }
    }
}