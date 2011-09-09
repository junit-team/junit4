/**
 * Created Oct 19, 2009
 */
package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;

/**
 * Tests to exercise class-level rules.
 */
public class ClassRulesMethodTest {
	public static class Counter extends ExternalResource {
		public int count = 0;

		@Override
		protected void before() throws Throwable {
			count++;
		}		
	}
	
	public static class ExampleTestWithClassRule {
		private static Counter counter= new Counter();
		
		@ClassRule
		public static Counter getCounter() {
			return counter;
		}

		@Test
		public void firstTest() {
			assertEquals(1, counter.count);
		}

		@Test
		public void secondTest() {
			assertEquals(1, counter.count);
		}
	}

	@Test
	public void ruleIsAppliedOnce() {
		ExampleTestWithClassRule.counter.count= 0;
		JUnitCore.runClasses(ExampleTestWithClassRule.class);
		assertEquals(1, ExampleTestWithClassRule.counter.count);
	}

	public static class SubclassOfTestWithClassRule extends
			ExampleTestWithClassRule {

	}

	@Test
	public void ruleIsIntroducedAndEvaluatedOnSubclass() {
		ExampleTestWithClassRule.counter.count= 0;
		JUnitCore.runClasses(SubclassOfTestWithClassRule.class);
		assertEquals(1, ExampleTestWithClassRule.counter.count);
	}
	
	public static class CustomCounter implements TestRule {
		public int count = 0;
		
		public Statement apply(final Statement base, Description description) {
			return new Statement() {				
				@Override
				public void evaluate() throws Throwable {
					count++;
					base.evaluate();
				}
			};
		}		
	}
	
	public static class ExampleTestWithCustomClassRule {
		private static CustomCounter counter= new CustomCounter();

		@ClassRule
		public static CustomCounter getCounter() {
			return counter;
		}

		@Test
		public void firstTest() {
			assertEquals(1, counter.count);
		}

		@Test
		public void secondTest() {
			assertEquals(1, counter.count);
		}
	}
	

	@Test
	public void customRuleIsAppliedOnce() {
		ExampleTestWithCustomClassRule.counter.count= 0;
		Result result= JUnitCore.runClasses(ExampleTestWithCustomClassRule.class);
		assertTrue(result.wasSuccessful());
		assertEquals(1, ExampleTestWithCustomClassRule.counter.count);
	}
}
