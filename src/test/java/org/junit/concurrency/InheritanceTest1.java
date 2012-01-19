package org.junit.concurrency;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ConcurrencyJunitRunner.class)
public class InheritanceTest1 {
	@Test
	public void assertConcurrencyTest() {
		Assert.assertTrue(foundConcurrencyFramework());
	}
	
	protected boolean foundConcurrencyFramework() {
		try {
			throw new Exception();
		} catch (Exception e) {
			for (StackTraceElement ste : e.getStackTrace()) {
				if (ste.getClassName().contains(ConcurrencyJunitRunner.class.getName())) {
					return true;
				}
			}
			return false;
		}
	}
}
