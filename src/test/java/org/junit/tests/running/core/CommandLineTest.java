package org.junit.tests.running.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.RealSystem;
import org.junit.runner.JUnitCore;

public class CommandLineTest {
	private ByteArrayOutputStream results;
	private PrintStream oldOut;
	private static boolean testWasRun;

	@Before public void before() { 
		oldOut= System.out;
		results= new ByteArrayOutputStream();
		System.setOut(new PrintStream(results));
	}

	@After public void after() {
		System.setOut(oldOut);
	}

	static public class Example {
		@Test public void test() { 
			testWasRun= true; 
		}
	}

	@Test public void runATest() {
		testWasRun= false; // todo create a TestSystem instead
		new JUnitCore().runMain(new RealSystem(), new String[]{"org.junit.tests.running.core.CommandLineTest$Example"});
		assertTrue(testWasRun);
	}
	
	@Test public void runAClass() {
		testWasRun= false;
		JUnitCore.runClasses(Example.class);
		assertTrue(testWasRun);		
	}

	private static int fCount;

	static public class Count {
		@Test public void increment() {
			fCount++;
		}
	}
	
	@Test public void runTwoClassesAsArray() {
		fCount= 0;
		JUnitCore.runClasses(new Class[] {Count.class, Count.class});
		assertEquals(2, fCount);		
	}

	@Test public void runTwoClasses() {
		fCount= 0;
		JUnitCore.runClasses(Count.class, Count.class);
		assertEquals(2, fCount);		
	}
}
