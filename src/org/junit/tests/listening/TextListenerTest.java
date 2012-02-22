package org.junit.tests.listening;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.tests.TestSystem;

public class TextListenerTest extends TestCase {
	
	private JUnitCore runner;
	private OutputStream results;
	private TextListener listener;

	@Override
	public void setUp() {
		runner= new JUnitCore();
		TestSystem system= new TestSystem();
		results= system.outContents();
		listener= new TextListener(system);
		runner.addListener(listener);
	}
	
	public static class OneTest {
		@Test public void one() {}
	}
	
	public void testSuccess() throws Exception {
		runner.run(OneTest.class);
		assertTrue(results.toString().startsWith(convert(".\nTime: ")));
		assertTrue(results.toString().endsWith(convert("\n\nOK (1 test)\n\n")));
	}
	
	public static class ErrorTest {
		@Test public void error() throws Exception {throw new Exception();}
	}
	
	public void testError() throws Exception {
		runner.run(ErrorTest.class);
		assertTrue(results.toString().startsWith(convert(".E\nTime: ")));
		assertTrue(results.toString().indexOf(convert("\nThere was 1 failure:\n1) error(org.junit.tests.listening.TextListenerTest$ErrorTest)\njava.lang.Exception")) != -1);
	}
	
	public static class Slow {
		@Test public void pause() throws InterruptedException {
			Thread.sleep(1000);
		}
	}
	
	public void testTime() {
		runner.run(Slow.class);
		assertFalse(results.toString().contains("Time: 0"));
	}
	
	private String convert(String string) {
		OutputStream resultsStream= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(resultsStream);
		writer.println();
		return string.replace("\n", resultsStream.toString());
	}
}
