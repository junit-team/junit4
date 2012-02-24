package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

@SuppressWarnings("deprecation")
public class BlockJUnit4ClassRunnerOverrideTest {
	public static class FlipBitRule implements MethodRule {
		public Statement apply(final Statement base, FrameworkMethod method,
				final Object target) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					target.getClass().getField("flipBit").set(target, true);
					base.evaluate();
				}
			};
		}

	}

	public static class OverrideRulesRunner extends BlockJUnit4ClassRunner {
		public OverrideRulesRunner(Class<?> klass) throws InitializationError {
			super(klass);
		}

		@Override
		protected List<MethodRule> rules(Object test) {
			final LinkedList<MethodRule> methodRules= new LinkedList<MethodRule>(
					super.rules(test));
			methodRules.add(new FlipBitRule());
			return methodRules;
		}
	}
	
	@RunWith(OverrideRulesRunner.class)
	public static class OverrideRulesTest {
		public boolean flipBit= false;

		@Test
		public void testFlipBit() {
			assertTrue(flipBit);
		}
	}

	@Test
	public void overrideRulesMethod() {
		assertThat(testResult(OverrideTestRulesTest.class), isSuccessful());
	}

	public static class OverrideTestRulesRunner extends BlockJUnit4ClassRunner {
		public OverrideTestRulesRunner(Class<?> klass)
				throws InitializationError {
			super(klass);
		}

		@Override
		protected List<TestRule> getTestRules(final Object test) {
			final LinkedList<TestRule> methodRules= new LinkedList<TestRule>(
					super.getTestRules(test));
			methodRules.add(new TestRule() {				
				public Statement apply(Statement base, Description description) {
					return new FlipBitRule().apply(base, null, test);
				}
			});
			return methodRules;
		}
	}

	@RunWith(OverrideTestRulesRunner.class)
	public static class OverrideTestRulesTest extends OverrideRulesTest {
	}

	@Test
	public void overrideTestRulesMethod() {
		assertThat(testResult(OverrideRulesTest.class), isSuccessful());
	}
}
