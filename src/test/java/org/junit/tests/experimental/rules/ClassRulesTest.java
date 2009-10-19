/**
 * Created Oct 19, 2009
 */
package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertFalse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ClassRule;
import org.junit.runner.JUnitCore;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * Tests to exercise class-level rules.
 */
public class ClassRulesTest {

	private static int runCount;

	public static class ExampleTestWithClassRule {

		@Rule
		public static ClassRule classRule= new ClassRule() {

			public Statement apply(final Statement base,
					final TestClass testClass) {
				return new Statement() {

					@Override
					public void evaluate() throws Throwable {
						++runCount;
						base.evaluate();
					}
				};
			}
		};

		@Test
		public void firstTest() {
			assertFalse("ClassRule was not applied!", runCount == 0);
			assertFalse("ClassRule was applied more than once!", runCount > 1);
		}

		@Test
		public void secondTest() {
			assertFalse("ClassRule was not applied!", runCount == 0);
			assertFalse("ClassRule was applied more than once!", runCount > 1);
		}
	}

	@Test
	public void ruleIsAppliedOnce() {
		runCount= 0;
		JUnitCore.runClasses(ExampleTestWithClassRule.class);
		assertFalse("ClassRule was not applied!", runCount == 0);
		assertFalse("ClassRule was applied more than once!", runCount > 1);
	}

	public static class SubclassOfTestWithClassRule extends
			ExampleTestWithClassRule {

	}

	@Test
	public void ruleIsIntroducedAndEvaluatedOnSubclass() {
		runCount= 0;
		JUnitCore.runClasses(SubclassOfTestWithClassRule.class);
		assertFalse("ClassRule was not applied!", runCount == 0);
		assertFalse("ClassRule was applied more than once!", runCount > 1);
	}

}
