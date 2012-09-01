package org.junit.tests.running.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class CommandLineTest {
	private ByteArrayOutputStream results;
	private PrintStream oldOut;
	private static boolean testWasRun;
	private static boolean test2WasRun;

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
		@Test public void test2() { 
			test2WasRun= true; 
		}
	}

	@Test public void runATest() {
		testWasRun= false;
		new MainRunner().runWithCheckForSystemExit(new Runnable() {
			public void run() {
				JUnitCore.main("org.junit.tests.running.core.CommandLineTest$Example");
			}
		});
		assertTrue(testWasRun);
	}

	@Test public void runSingleMethod() {
		testWasRun = false;
		new MainRunner().runWithCheckForSystemExit(new Runnable() {
			public void run() {
				JUnitCore.main("org.junit.tests.running.core.CommandLineTest$Example#test");
			}
		});
		assertTrue(testWasRun);
	}

	@Test public void runTwoMethods() {
		testWasRun = false;
		test2WasRun = false;
		new MainRunner().runWithCheckForSystemExit(new Runnable() {
			public void run() {
				JUnitCore.main("org.junit.tests.running.core.CommandLineTest$Example#test"
						, "org.junit.tests.running.core.CommandLineTest$Example#test2");
			}
		});
		assertTrue(testWasRun);
		assertTrue(test2WasRun);
	}

	@Test public void runTwoMethodsWithWildcard() {
		testWasRun = false;
		test2WasRun = false;
		new MainRunner().runWithCheckForSystemExit(new Runnable() {
			public void run() {
				JUnitCore.main("org.junit.tests.running.core.CommandLineTest$Example#t.*st.*");
			}
		});
		assertTrue(testWasRun);
		assertTrue(test2WasRun);
	}

	@Test public void missingMethod() {
		testWasRun = false;
		test2WasRun = false;
		new MainRunner().runWithCheckForSystemExit(new Runnable() {
			public void run() {
				JUnitCore.main("org.junit.tests.running.core.CommandLineTest$Example#");
			}
		});
		assertTrue(testWasRun);
		assertTrue(test2WasRun);
	}

	@Test public void missingClass() {
		testWasRun = false;
		test2WasRun = false;
		new MainRunner().runWithCheckForSystemExit(new Runnable() {
			public void run() {
				JUnitCore.main("#test");
			}
		});
		assertFalse(testWasRun);
		assertFalse(test2WasRun);
	}

	@Test public void donotRunSingleMethod() {
		testWasRun = false;
		test2WasRun = false;
		new MainRunner().runWithCheckForSystemExit(new Runnable() {
			public void run() {
				JUnitCore.main("org.junit.tests.running.core.CommandLineTest$Example#tst");
			}
		});
		assertFalse(testWasRun);
		assertFalse(test2WasRun);
	}

	@Test public void donotRunWildcardMethod() {
		testWasRun = false;
		test2WasRun = false;
		new MainRunner().runWithCheckForSystemExit(new Runnable() {
			public void run() {
				JUnitCore.main("org.junit.tests.running.core.CommandLineTest$Example#tssdft*");
			}
		});
		assertFalse(testWasRun);
		assertFalse(test2WasRun);
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
