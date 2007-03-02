package org.junit.tests;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.Invariant;
import org.junit.runner.JUnitCore;


public class InvariantTest {
	static String log;
	@Test public void makeSureInvariantsAreRun() {
		log= "";
		JUnitCore.runClasses(Example.class);
		assertEquals("before invariant test invariant after ", log);
	}
	
	public static class Example {
		@Before public void before() {
			log+= "before ";
		}
		@After public void after() {
			log+= "after ";
		}
		@Invariant public void invariant() {
			log+= "invariant ";
		}
		@Test public void test() {
			log+= "test ";
		}
	}
}
