package org.junit.tests;


import static org.junit.Assert.assertEquals;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

public class PreJUnit4TestCaseRunnerTest {

	static int count;
	
	static public class OneTest extends TestCase {
		public void testOne() {
		}
	}
	
	@Test public void testListener() throws Exception {
		JUnitCore runner= new JUnitCore();
		RunListener listener= new RunListener() {
			@Override
			public void testStarted(Description description) {
				assertEquals(Description.createTestDescription(OneTest.class, "testOne"), 
						description);
				count++;
			}
		};
		
		runner.addListener(listener);
		count= 0;
		Result result= runner.run(OneTest.class);
		assertEquals(1, count);
		assertEquals(1, result.getRunCount());
	}
}
