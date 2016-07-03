package org.junit.internal.runners.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.fixtures.FixtureManager;
import org.junit.fixtures.InstanceMethod;
import org.junit.fixtures.TestFixture;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * Adapter that allows {@link TestFixture} instances to be run a rule.
 */
public class RunFixtures implements TestRule, MethodRule {
    private final List<TestFixture> testFixtures;

    public RunFixtures(List<TestFixture> testFixtures) {
        this.testFixtures = new ArrayList<TestFixture>(testFixtures);
    }

    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new FixtureInstallingStatement(base, method, target);
    }

    public Statement apply(Statement base, Description description) {
        return new FixtureInstallingStatement(base, description.getTestClass());
    }
 
    private class FixtureInstallingStatement extends Statement  {
        private final Statement baseStatement;
        private final FixtureManager fixtureManager;

        public FixtureInstallingStatement(Statement baseStatement, Class<?> testClass) {
            this.baseStatement = baseStatement;
            fixtureManager = FixtureManager.forTestClass(testClass);
        }

        public FixtureInstallingStatement(Statement baseStatement, FrameworkMethod method, Object target) {
            this.baseStatement = baseStatement;
            fixtureManager = FixtureManager.forTestMethod(new InstanceMethod(method.getMethod(), target));
        }

        @Override
        public void evaluate() throws Throwable {
            for (TestFixture testFixture : testFixtures) {
                fixtureManager.initializeFixture(testFixture);
            }

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
