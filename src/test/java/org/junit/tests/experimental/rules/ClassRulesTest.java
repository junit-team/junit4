/**
 * Created Oct 19, 2009
 */
package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertEquals;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.JUnitCore;

/**
 * Tests to exercise class-level rules.
 */
public class ClassRulesTest {
	public static class Counter extends ExternalResource {
		public int count = 0;

		@Override
		protected void before() throws Throwable {
			count++;
		}		
	}
	
	public static class ExampleTestWithClassRule {
		@ClassRule
		public static Counter counter= new Counter();

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
}
