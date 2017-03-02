package org.junit.concurrency;

import org.junit.Assert;
import org.junit.Test;

public class InheritanceTest2 extends InheritanceTest1 {
	@Override
	@Test
	public void assertConcurrencyTest() {
		Assert.assertTrue(foundConcurrencyFramework());
	}
}
