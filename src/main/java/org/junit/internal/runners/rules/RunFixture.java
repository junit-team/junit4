package org.junit.internal.runners.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.fixtures.FixtureManager;
import org.junit.fixtures.TestFixture;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * Adapter that allows a {@link TestFixture} to be used as a rule.
 */
public class RunFixture implements TestRule {
    private final TestFixture testFixture;

    public RunFixture(TestFixture testFixture) {
        this.testFixture = testFixture;
    }

    public Statement apply(Statement base, Description description) {
        return new FixtureInstallingStatement(base);
    }
 
    private class FixtureInstallingStatement extends Statement  {
        private final Statement baseStatement;

        public FixtureInstallingStatement(Statement baseStatement) {
            this.baseStatement = baseStatement;
        }

        @Override
        public void evaluate() throws Throwable {
            FixtureManager fixtureManager = new FixtureManager();
            fixtureManager.initializeFixture(testFixture);

            List<Throwable> errors = new ArrayList<Throwable>();
            try {
                baseStatement.evaluate();
                fixtureManager.runAllPostconditions();
            } catch (Throwable e) {
                errors.add(e);
            } finally {
                fixtureManager.runAllTearDowns(errors);
            }
            MultipleFailureException.assertEmpty(errors);
        }
    }
}
