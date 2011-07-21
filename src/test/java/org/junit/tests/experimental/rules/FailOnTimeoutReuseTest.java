package org.junit.tests.experimental.rules;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.runners.model.Statement;

/**
 * Tests evaluation of the FailOnTimeout statement
 * when statement is reused.
 * 
 * @author Asaf Ary, Stefan Birkner
 */
public class FailOnTimeoutReuseTest {
	
	@Test
	public void testReusePassedStatement() {
		ExceptionStatement eStatement= new ExceptionStatement();
		FailOnTimeout fot= new FailOnTimeout(eStatement, 1000);
		try {
			fot.evaluate();
		} catch (Throwable t) {
			fail("first attempt passes");
		}
		eStatement.failNext();
		boolean failed= false;
		try {
			fot.evaluate();
		} catch (Throwable t) {
			failed= true;
		}
		assertTrue(failed);
	}
	
	@Test
	public void testReuseThrownExceptionStatement() {
		ExceptionStatement eStatement= new ExceptionStatement();
		FailOnTimeout fot= new FailOnTimeout(eStatement, 100);
		
		eStatement.failNext();
		try {
			fot.evaluate();
			fail("exception is thrown");
		} catch (Throwable t) {
			assertEquals("fail on demand", t.getMessage());
		}
		eStatement.waitFor(200);
		try {
			fot.evaluate();
			fail("test failed");
		} catch (Throwable t) {
			assertTrue("timeout exception thrown", t.getMessage().contains("timed out"));
		}
	}
	
	private static class ExceptionStatement extends Statement {
		
		private boolean fail= false;
		private long waitTime = 0;
		
		@Override
		public void evaluate() throws Throwable {
			if (waitTime > 0)
				Thread.sleep(waitTime);
			if (fail)
				fail("fail on demand");
		}
		
		private void failNext() {
			this.fail= true;
		}
		
		private void waitFor(long time) {
			this.waitTime = time;
		}
		
	}
}
